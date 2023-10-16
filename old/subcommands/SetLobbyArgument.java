package me.dreamdevs.github.huntergame.old.subcommands;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.commands.ArgumentCommand;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyArgument implements ArgumentCommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ColourUtil.colorize("&cOnly players can perform this command!"));
            return true;
        }
        HunterGameMain.getInstance().getLobby().setLobbyLocation(((Player)commandSender).getLocation());
        HunterGameMain.getInstance().getLobby().saveLobby();
        commandSender.sendMessage(ColourUtil.colorize("&aLobby has set to your position!"));
        return true;
    }

    @Override
    public String getHelpText() {
        return "&a/huntergame setlobby - sets the lobby location to the player's location";
    }

    @Override
    public String getPermission() {
        return "huntergame.admin";
    }
}