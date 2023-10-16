package me.dreamdevs.github.huntergame.old.subcommands;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.commands.ArgumentCommand;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ArenaEditArgument implements ArgumentCommand {
    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ColourUtil.colorize("&cConsole cannot perform this command!"));
            return true;
        }
        if(args.length <= 1) {
             commandSender.sendMessage(ColourUtil.colorize("&cToo little to set something!"));
             return true;
        }
        String id = args[1];
        Player player = (Player)commandSender;
        Game game = HunterGameMain.getInstance().getGameManager().getGames().stream().filter(g -> g.getId().equalsIgnoreCase(id)).findFirst().get();
        if(game == null) {
            player.sendMessage(ColourUtil.colorize("&cThere's no arena with this ID."));
            return true;
        }
        GUI gui = new GUI("Edit: "+game.getId(), GUISize.ONE_ROW);

        GItem time = new GItem(Material.CLOCK, "&aTime: {TIME}".replace("{TIME}", String.valueOf(game.getGameTime())), ColourUtil.colouredLore("", "&7Click to set the time."));
        GItem goal = new GItem(Material.APPLE, "&aGoal: "+game.getGoal(), ColourUtil.colouredLore("", "&7Click to set the goal."));
        GItem minPlayers = new GItem(Material.REDSTONE, "&aMinimum Players: "+game.getMinPlayers(), ColourUtil.colouredLore("", "&7Click to set the minimum players."));
        GItem maxPlayers = new GItem(Material.COAL, "&aMaximum Players: "+game.getMaxPlayers(), ColourUtil.colouredLore("", "&7Click to set the maximum players."));
        GItem spawnLocation = new GItem(Material.BEACON, "&aSpawn Location", ColourUtil.colouredLore("", "&7Click to set spawn location!"));
        GItem mobsLocation = new GItem(Material.DANDELION, "&aAdd Mob Spawn Location", ColourUtil.colouredLore("", "&7Add mob spawn location!"));
        GItem spawnMobsTime = new GItem(Material.BONE, "&aSpawn Mobs Time: {MOBS_TIME}".replace("{MOBS_TIME}", String.valueOf(game.getSpawnMobsTime())), ColourUtil.colouredLore("&7Click to set the spawn mobs time."));
        GItem saveAndLoad = new GItem(Material.ANVIL, "&a&lSave and Load", new ArrayList<>());

        time.addActions(new CloseAction(), event -> SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new time (only integers, like: 60):"), new AcceptAnswer() {
            @Override
            public boolean onAnswer(String input) {
                try {

                    game.setGameTime(Integer.parseInt(input));
                    HunterGameMain.getInstance().getGameManager().saveGame(game);
                    HunterGameMain.getInstance().getGameManager().stop(game);
                    HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                    player.sendMessage(ColourUtil.colorize("&aYou set the time to "+Integer.parseInt(input)+"!"));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }));
        goal.addActions(new CloseAction(), event -> SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new goal (only integers, like: 35):"), new AcceptAnswer() {
            @Override
            public boolean onAnswer(String input) {
                try {

                    game.setGoal(Integer.parseInt(input));
                    HunterGameMain.getInstance().getGameManager().saveGame(game);
                    HunterGameMain.getInstance().getGameManager().stop(game);
                    HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                    player.sendMessage(ColourUtil.colorize("&aYou set the goal to "+Integer.parseInt(input)+"!"));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }));
        minPlayers.addActions(new CloseAction(), event -> SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new minimum players requirement (only integers, like: 4):"), new AcceptAnswer() {
            @Override
            public boolean onAnswer(String input) {
                try {

                    game.setMinPlayers(Integer.parseInt(input));
                    HunterGameMain.getInstance().getGameManager().saveGame(game);
                    HunterGameMain.getInstance().getGameManager().stop(game);
                    HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                    player.sendMessage(ColourUtil.colorize("&aYou set the minimum players to "+Integer.parseInt(input)+"!"));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }));
        maxPlayers.addActions(new CloseAction(), event -> SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new maximum players requirement (only integers, like: 12):"), input -> {
            try {
                game.setMaxPlayers(Integer.parseInt(input));
                HunterGameMain.getInstance().getGameManager().saveGame(game);
                HunterGameMain.getInstance().getGameManager().stop(game);
                HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                player.sendMessage(ColourUtil.colorize("&aYou set the maximum players to "+Integer.parseInt(input)+"!"));
                return true;
            } catch (Exception e) {
                return false;
            }
        }));
        spawnMobsTime.addActions(new CloseAction(), event -> SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new spawn mobs time (only integers, like: 3):"), input -> {
            try {
                game.setSpawnMobsTime(Integer.parseInt(input));
                HunterGameMain.getInstance().getGameManager().saveGame(game);
                HunterGameMain.getInstance().getGameManager().stop(game);
                HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                player.sendMessage(ColourUtil.colorize("&aYou set the spawn mobs time to "+Integer.parseInt(input)+"!"));
                return true;
            } catch (Exception e) {
                return false;
            }
        }));
        spawnLocation.addActions(new CloseAction(), event -> {
            player.sendMessage(ColourUtil.colorize("&aYou set the spawn location!"));
            game.setStartSpawnLocation(player.getLocation());
        });
        mobsLocation.addActions(new CloseAction(), event -> SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input the mob type (COW/PIG/CHICKEN):"), new AcceptAnswer() {
            @Override
            public boolean onAnswer(String input) {
                if(input.equalsIgnoreCase("cow") || input.equalsIgnoreCase("chicken") || input.equalsIgnoreCase("pig")) {
                    game.getMobsLocations().put(player.getLocation(), input.toUpperCase());
                    HunterGameMain.getInstance().getGameManager().saveGame(game);
                    HunterGameMain.getInstance().getGameManager().stop(game);
                    HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                    player.sendMessage(ColourUtil.colorize("&a&aYou add the mob spawn location!"));
                    return true;
                }
                return false;

            }
        }));
        saveAndLoad.addActions(new CloseAction(), event -> {
            HunterGameMain.getInstance().getGameManager().saveGame(game);
            HunterGameMain.getInstance().getGameManager().stop(game);
            HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
            player.sendMessage(ColourUtil.colorize("&aRestarted all settings!"));
        });
        gui.setItem(0, time);
        gui.setItem(1, goal);
        gui.setItem(2, minPlayers);
        gui.setItem(3, maxPlayers);
        gui.setItem(4, spawnLocation);
        gui.setItem(5, mobsLocation);
        gui.setItem(6, spawnMobsTime);
        gui.setItem(8, saveAndLoad);
        gui.openGUI(player);
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
