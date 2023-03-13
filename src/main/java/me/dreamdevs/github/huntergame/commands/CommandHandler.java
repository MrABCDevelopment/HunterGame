package me.dreamdevs.github.huntergame.commands;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.commands.subcommands.ArenaCreateArgument;
import me.dreamdevs.github.huntergame.commands.subcommands.ArenaDeleteArgument;
import me.dreamdevs.github.huntergame.commands.subcommands.ArenaEditArgument;
import me.dreamdevs.github.huntergame.commands.subcommands.SetLobbyArgument;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements TabExecutor {

    private @Getter HashMap<String, Class<? extends ArgumentCommand>> arguments;

    public CommandHandler(HunterGameMain plugin) {
        this.arguments = new HashMap<>();
        arguments.put("setlobby", SetLobbyArgument.class);
        arguments.put("arenacreate", ArenaCreateArgument.class);
        arguments.put("arenaedit", ArenaEditArgument.class);
        arguments.put("arenadelete", ArenaDeleteArgument.class);
        plugin.getCommand("huntergame").setExecutor(this);
        plugin.getCommand("huntergame").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            if(strings.length >= 1) {
                if(arguments.containsKey(strings[0])) {
                    Class<? extends ArgumentCommand> argumentCommand = arguments.get(strings[0]).asSubclass(ArgumentCommand.class);
                    ArgumentCommand argument = argumentCommand.newInstance();
                    if(commandSender.hasPermission(argument.getPermission())) {
                        argument.execute(commandSender, strings);
                    } else {
                        commandSender.sendMessage(ColourUtil.colorize("&cYou don't have permission to do this!"));
                    }
                    return true;
                } else {
                    commandSender.sendMessage(ColourUtil.colorize("&cArgument doesn't exist!"));
                    return true;
                }
            } else {
                commandSender.sendMessage(ColourUtil.colorize("&aHelp for &a&lHunter&e&lGame:"));
                for(Class<? extends ArgumentCommand> argumentCommand : arguments.values()) {
                    commandSender.sendMessage(ColourUtil.colorize(argumentCommand.newInstance().getHelpText()));
                }
                return true;
            }
        } catch (Exception ignored) {

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();
        if(strings.length == 1) {
            StringUtil.copyPartialMatches(strings[0], arguments.keySet(), completions);
            Collections.sort(completions);
            return completions;
        } else
            return Collections.emptyList();
    }
}