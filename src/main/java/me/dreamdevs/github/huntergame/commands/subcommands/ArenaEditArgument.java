package me.dreamdevs.github.huntergame.commands.subcommands;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.commands.ArgumentCommand;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaEditArgument implements ArgumentCommand {
    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if(args.length <= 1) {
             commandSender.sendMessage(ColourUtil.colorize("&cToo little to set something!"));
             return true;
        }
        String id = args[1];
        Player player = (Player)commandSender;
        Game game = HunterGameMain.getInstance().getGameManager().getGames().stream().filter(g -> g.getId().equalsIgnoreCase(id)).findFirst().get();
        if(args.length == 3) {
            if (args[2].equalsIgnoreCase("setspawn")) {
                game.setStartSpawnLocation(player.getLocation());
                player.sendMessage(ColourUtil.colorize("&aYou set new spawn location!"));
                return true;
            }
            if (args[2].equalsIgnoreCase("addmobspawn")) {
                game.getMobsSpawnLocations().add(player.getLocation());
                player.sendMessage(ColourUtil.colorize("&aYou added new mob spawn location!"));
                return true;
            }
            if (args[2].equalsIgnoreCase("save")) {
                game.restartAllSettings();
                player.sendMessage(ColourUtil.colorize("&aRestarted all settings!"));
                return true;
            }
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("time")) {
                try {
                    int time = Integer.parseInt(args[3]);
                    game.setTime(time);
                    player.sendMessage(ColourUtil.colorize("&aYou set game time!"));
                } catch (Exception e) {
                    player.sendMessage(ColourUtil.colorize("&cOnly integers can be used here!"));
                }
                return true;
            }
            if (args[2].equalsIgnoreCase("goal")) {
                try {
                    int goal = Integer.parseInt(args[3]);
                    game.setTime(goal);
                    player.sendMessage(ColourUtil.colorize("&aYou set game goal!"));
                } catch (Exception e) {
                    player.sendMessage(ColourUtil.colorize("&cOnly integers can be used here!"));
                }
                return true;
            }
            if (args[2].equalsIgnoreCase("minplayers")) {
                try {
                    int minplayers = Integer.parseInt(args[3]);
                    game.setMinPlayers(minplayers);
                    player.sendMessage(ColourUtil.colorize("&aYou set game min players!"));
                } catch (Exception e) {
                    player.sendMessage(ColourUtil.colorize("&cOnly integers can be used here!"));
                }
                return true;
            }
            if (args[2].equalsIgnoreCase("maxplayers")) {
                try {
                    int maxplayers = Integer.parseInt(args[3]);
                    game.setMaxPlayers(maxplayers);
                    player.sendMessage(ColourUtil.colorize("&aYou set game max players!"));
                } catch (Exception e) {
                    player.sendMessage(ColourUtil.colorize("&cOnly integers can be used here!"));
                }
                return true;
            } else {
                player.sendMessage(ColourUtil.colorize("&cSet something properly..."));
            }
        } else {
            player.sendMessage(ColourUtil.colorize("&cSet something properly..."));
        }
        return true;
    }

    @Override
    public String getHelpText() {
        return "&a/huntergame arenaedit <id> <setting> [value]";
    }

    @Override
    public String getPermission() {
        return "huntergame.admin";
    }
}
