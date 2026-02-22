package org.cat.shoolpluginmy;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
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
            Player player = (Player) sender;
            if (args[0] == null) {
                player.sendMessage("Введиту аргумент 1 названия головы в конфигурации");
                return false;
            }
            String pacman = args[0];
            FileConfiguration cofig = ShoolPluginMy.getPlugin(ShoolPluginMy.class).getConfig();
            if (cofig.getBoolean("Heads." + pacman + ".permission") == true) {
                if (!sender.hasPermission("createhead.admin")) {
                    player.sendMessage(cofig.getString("ru.text-not-permission"));
                    return true;
                }
            }
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            getCustomSkull(cofig.getString("Heads." + pacman + ".texture"),item);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = cofig.getStringList("Heads." + pacman + ".lore");
            meta.setDisplayName(cofig.getString("Heads." + pacman + ".name"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
            ShoolPluginMy.addValue(player,1);
            return true;
    }

    public ItemStack getCustomSkull(String base64,ItemStack item) {
        if (base64.isEmpty()) return item;

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        item.setItemMeta(skullMeta);
        return item;
    }
}
