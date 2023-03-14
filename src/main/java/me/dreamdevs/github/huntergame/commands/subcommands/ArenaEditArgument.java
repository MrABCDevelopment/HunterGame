package me.dreamdevs.github.huntergame.commands.subcommands;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.inventory.GItem;
import me.dreamdevs.github.huntergame.api.inventory.GUI;
import me.dreamdevs.github.huntergame.api.inventory.GUISize;
import me.dreamdevs.github.huntergame.api.inventory.actions.CloseAction;
import me.dreamdevs.github.huntergame.commands.ArgumentCommand;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;

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
        GUI gui = new GUI("Edit: "+game.getId(), GUISize.ONE_ROW);

        GItem time = new GItem(Material.CLOCK, "&aTime: {TIME}".replace("{TIME}", String.valueOf(game.getTime())), ColourUtil.colouredLore("", "&7Left-click to add 1 to time", "&7Right-click to remove 1 from time"));
        GItem goal = new GItem(Material.APPLE, "&aGoal: "+game.getGoal(), ColourUtil.colouredLore("", "&7Left-click to add 1 to goal", "&7Right-click to remove 1 from goal"));
        GItem minPlayers = new GItem(Material.REDSTONE, "&aMinimum Players: "+game.getMinPlayers(), ColourUtil.colouredLore("", "&7Left-click to add 1 to minimum players", "&7Right-click to remove 1 from minimum players"));
        GItem maxPlayers = new GItem(Material.COAL, "&aMaximum Players: "+game.getMaxPlayers(), ColourUtil.colouredLore("", "&7Left-click to add 1 to maximum players", "&7Right-click to remove 1 from maximum players"));
        GItem spawnLocation = new GItem(Material.BEACON, "&aSpawn Location", ColourUtil.colouredLore("", "&7Click to set spawn location!"));
        GItem mobsLocation = new GItem(Material.DANDELION, "&aAdd Mob Spawn Location", ColourUtil.colouredLore("", "&7Add mob spawn location!"));
        GItem saveAndLoad = new GItem(Material.ANVIL, "&a&lSave and Load", new ArrayList<>());
        time.addAction(event -> {
            player.closeInventory();
            SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new time (only integers, like: 60):"), new AcceptAnswer() {
                @Override
                public boolean onAnswer(String input) {
                    if(Integer.getInteger(input) != null) {

                        game.setTime(Integer.parseInt(input));
                        HunterGameMain.getInstance().getGameManager().saveGame(game);
                        HunterGameMain.getInstance().getGameManager().stop(game);
                        HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                        player.sendMessage(ColourUtil.colorize("&aYou set the time to "+Integer.parseInt(input)+"!"));
                        return true;
                        //player.sendMessage(ColourUtil.colorize("&cYou can use here only integers!"));
                        //return false;
                    }
                    return false;
                }
            });
        });
        goal.addAction(event -> {
            player.closeInventory();
            SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new goal (only integers, like: 35):"), new AcceptAnswer() {
                @Override
                public boolean onAnswer(String input) {
                    try {

                        game.setTime(Integer.parseInt(input));
                        HunterGameMain.getInstance().getGameManager().saveGame(game);
                        HunterGameMain.getInstance().getGameManager().stop(game);
                        HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                        player.sendMessage(ColourUtil.colorize("&aYou set the goal to "+Integer.parseInt(input)+"!"));
                        return true;
                        //player.sendMessage(ColourUtil.colorize("&cYou can use here only integers!"));
                        //return false;
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
        });
        minPlayers.addAction(event -> {
            player.closeInventory();
            SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new minimum players requirement (only integers, like: 4):"), new AcceptAnswer() {
                @Override
                public boolean onAnswer(String input) {
                    try {

                        game.setTime(Integer.parseInt(input));
                        HunterGameMain.getInstance().getGameManager().saveGame(game);
                        HunterGameMain.getInstance().getGameManager().stop(game);
                        HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                        player.sendMessage(ColourUtil.colorize("&aYou set the minimum players to "+Integer.parseInt(input)+"!"));
                        return true;
                        //player.sendMessage(ColourUtil.colorize("&cYou can use here only integers!"));
                        //return false;
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
        });
        maxPlayers.addAction(event -> {
            player.closeInventory();
            SingleQuestionPrompt.newPrompt(player, ColourUtil.colorize("&7Input new maximum players requirement (only integers, like: 12):"), new AcceptAnswer() {
                @Override
                public boolean onAnswer(String input) {
                    try {

                        game.setTime(Integer.parseInt(input));
                        HunterGameMain.getInstance().getGameManager().saveGame(game);
                        HunterGameMain.getInstance().getGameManager().stop(game);
                        HunterGameMain.getInstance().getGameManager().loadGame(game.getFile());
                        player.sendMessage(ColourUtil.colorize("&aYou set the maximum players to "+Integer.parseInt(input)+"!"));
                        return true;
                        //player.sendMessage(ColourUtil.colorize("&cYou can use here only integers!"));
                        //return false;
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
        });
        spawnLocation.addActions(new CloseAction(), event -> {
            player.sendMessage(ColourUtil.colorize("&aYou set the spawn location!"));
            game.setStartSpawnLocation(player.getLocation());
        });
        mobsLocation.addActions(new CloseAction(), event -> {
            player.sendMessage(ColourUtil.colorize("&aYou add the mob spawn location!"));
            game.getMobsSpawnLocations().add(player.getLocation());
        });
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
        gui.setItem(6, saveAndLoad);
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
