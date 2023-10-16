package me.dreamdevs.github.huntergame.old.subcommands;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.commands.ArgumentCommand;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCreateArgument implements ArgumentCommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if(args[1] == null) {
            commandSender.sendMessage(ColourUtil.colorize("&cThere's no ID! Type any id to create an arena!"));
            return true;
        }
        Game game = new Game(args[1]);
        game.setGameTime(50);
        game.setMaxPlayers(8);
        game.setMinPlayers(2);
        game.setGameState(GameState.WAITING);
        game.setGoal(30);
        game.setSpawnMobsTime(3);
        game.setGameType(GameType.CLASSIC);
        game.setStartSpawnLocation(((Player)commandSender).getLocation());
        HunterGameMain.getInstance().getGameManager().getGames().add(game);
        game.startGame();
        HunterGameMain.getInstance().getGameManager().saveGame(game);
        commandSender.sendMessage(ColourUtil.colorize("&aYou created new arena with ID: "+game.getId()+"!"));
        return true;
    }

    @Override
    public String getHelpText() {
        return "&a/hungerhunt arenacreate <id> - creates an arena with specific ID";
    }

    @Override
    public String getPermission() {
        return "huntergame.admin";
    }
}