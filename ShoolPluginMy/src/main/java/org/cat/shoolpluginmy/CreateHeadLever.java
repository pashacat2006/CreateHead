package org.cat.shoolpluginmy;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreateHeadLever implements TabCompleter {
    private int i;

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            FileConfiguration config = ShoolPluginMy.getPlugin(ShoolPluginMy.class).getConfig();
            List<String> names = config.getStringList("Names");
            ArrayList<String> list = new ArrayList<>();
            for (String name : names) {
                list.add(name);
            }
            return list;
        }
        return new ArrayList<>();
    }
}
