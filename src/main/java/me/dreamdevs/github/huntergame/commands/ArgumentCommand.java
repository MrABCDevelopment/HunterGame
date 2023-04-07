package me.dreamdevs.github.huntergame.commands;

import org.bukkit.command.CommandSender;

public interface ArgumentCommand {

    boolean execute(CommandSender commandSender, String[] args);

    String getHelpText();

    String getPermission();

}