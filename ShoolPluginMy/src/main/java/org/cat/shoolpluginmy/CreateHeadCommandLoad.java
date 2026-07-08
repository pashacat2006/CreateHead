package org.cat.shoolpluginmy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class CreateHeadCommandLoad implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = ShoolPluginMy.getPlugin(ShoolPluginMy.class).getConfig();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Введите название головы из конфигурации или reload");
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("createhead.reload")) {
                sender.sendMessage(color(config.getString("ru.text-not-permission")));
                return true;
            }
            ShoolPluginMy.getInstance().reloadPluginConfig();
            sender.sendMessage(color(config.getString("ru.text-reload-success")));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        String headName = args[0];

        if (!config.isConfigurationSection("Heads." + headName)) {
            player.sendMessage(color(config.getString("ru.text-head-not-found", "&cГолова не найдена в конфигурации.")));
            return true;
        }

        if (config.getBoolean("Heads." + headName + ".permission")) {
            if (!sender.hasPermission("createhead.admin")) {
                player.sendMessage(color(config.getString("ru.text-not-permission")));
                return true;
            }
        }

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        getCustomSkull(config.getString("Heads." + headName + ".texture"), item);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = config.getStringList("Heads." + headName + ".lore");
        meta.setDisplayName(color(config.getString("Heads." + headName + ".name")));
        meta.setLore(colorList(lore));
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        ShoolPluginMy.addValue(player, 1);
        return true;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }

    private List<String> colorList(List<String> lines) {
        List<String> colored = new java.util.ArrayList<>();
        for (String line : lines) {
            colored.add(color(line));
        }
        return colored;
    }

    public ItemStack getCustomSkull(String base64, ItemStack item) {
        if (base64 == null || base64.isEmpty()) {
            return item;
        }

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Method method = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            method.setAccessible(true);
            method.invoke(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        item.setItemMeta(skullMeta);
        return item;
    }
}
