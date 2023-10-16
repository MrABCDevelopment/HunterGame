package me.dreamdevs.github.huntergame.commands;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
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

    private final @Getter HashMap<String, Class<? extends ArgumentCommand>> arguments;

    public CommandHandler(HunterGameMain plugin) {
        this.arguments = new HashMap<>();
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
                        commandSender.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("no-permission"));
                    }
                    return true;
                } else {
                    commandSender.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("no-argument"));
                    return true;
                }
            } else {
                commandSender.sendMessage(ColourUtil.colorize("&aHelp for HunterGame:"));
                for(Class<? extends ArgumentCommand> argumentCommand : arguments.values()) {
                    commandSender.sendMessage(ColourUtil.colorize(argumentCommand.newInstance().getHelpText()));
                }
                return true;
            }
        } catch (Exception e) {

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
        } else return Collections.emptyList();
    }

    public void registerCommand(String command, Class<? extends ArgumentCommand> clazz) {
        arguments.put(command, clazz);
    }

}