package org.cat.shoolpluginmy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateHeadLever implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        FileConfiguration config = ShoolPluginMy.getPlugin(ShoolPluginMy.class).getConfig();
        List<String> suggestions = new ArrayList<>();

        if (commandSender.hasPermission("createhead.reload") && "reload".startsWith(args[0].toLowerCase())) {
            suggestions.add("reload");
        }

        for (String name : config.getStringList("Names")) {
            if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                suggestions.add(name);
            }
        }

        return suggestions;
    }
}
