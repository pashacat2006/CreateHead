package org.cat.shoolpluginmy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;

public final class ShoolPluginMy extends JavaPlugin {
    private static final ConcurrentHashMap<Player, Integer> playersData = new ConcurrentHashMap();
    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveConfig();
        getCommand("CreateHead").setExecutor(new CreateHeadCommandLoad());
        getCommand("CreateHead").setTabCompleter(new CreateHeadLever());
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            // Регистрируем наш класс.
            new Placeholders().register();
        }
    }
    public static void addValue(Player p, int value) {
        playersData.putIfAbsent(p, playersData.getOrDefault(p, 0) + value);
    }
    public static int getValue(Player p) {
        return playersData.getOrDefault(p, 0);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

}
