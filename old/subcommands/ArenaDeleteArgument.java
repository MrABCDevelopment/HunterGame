package me.dreamdevs.github.huntergame.old.subcommands;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.commands.ArgumentCommand;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.command.CommandSender;

public class ArenaDeleteArgument implements ArgumentCommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if(args.length <= 1) {
            commandSender.sendMessage(ColourUtil.colorize("&cPlease type arena ID!"));
            return true;
        }
        Game game = HunterGameMain.getInstance().getGameManager().getGames().stream().filter(g -> g.getId().equalsIgnoreCase(args[1])).findFirst().get();
        HunterGameMain.getInstance().getGameManager().getGames().remove(game);
        commandSender.sendMessage(ColourUtil.colorize("&aDeleted arena with ID: "+game.getId()));
        return true;
    }

    @Override
    public String getHelpText() {
        return "&a/huntergame arenadelete <id> - deletes arena with specific ID";
    }

    @Override
    public String getPermission() {
        return "huntergame.admin";
    }
}