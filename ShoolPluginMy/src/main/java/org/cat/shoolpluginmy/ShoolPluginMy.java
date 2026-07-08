package org.cat.shoolpluginmy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;

public final class ShoolPluginMy extends JavaPlugin implements Listener {
    private static ShoolPluginMy instance;
    private static final ConcurrentHashMap<Player, Integer> playersData = new ConcurrentHashMap<>();
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        getCommand("CreateHead").setExecutor(new CreateHeadCommandLoad());
        getCommand("CreateHead").setTabCompleter(new CreateHeadLever());
        getServer().getPluginManager().registerEvents(this, this);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders().register();
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        databaseManager.connect();
        playersData.clear();
        for (Player player : getServer().getOnlinePlayers()) {
            loadPlayerData(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayerData(event.getPlayer());
    }

    private void loadPlayerData(Player player) {
        if (databaseManager.isEnabled()) {
            playersData.put(player, databaseManager.getCount(player.getUniqueId()));
        } else {
            playersData.putIfAbsent(player, 0);
        }
    }

    public static void addValue(Player player, int value) {
        int newValue = getValue(player) + value;
        playersData.put(player, newValue);
        ShoolPluginMy plugin = getPlugin(ShoolPluginMy.class);
        if (plugin.databaseManager.isEnabled()) {
            plugin.databaseManager.saveCount(player.getUniqueId(), player.getName(), newValue);
        }
    }

    public static int getValue(Player player) {
        return playersData.getOrDefault(player, 0);
    }

    public static ShoolPluginMy getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
    }
}
