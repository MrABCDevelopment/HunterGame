package me.dreamdevs.github.huntergame.managers;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.game.GameState;
import me.dreamdevs.github.huntergame.utils.CustomItem;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameManager {

    private List<Game> games;

    public GameManager() {
        games = new ArrayList<>();
        File file = new File(HunterGameMain.getInstance().getDataFolder(), "arenas");
        if(!file.exists() || !file.isDirectory())
            file.mkdirs();
        if(file.listFiles().length == 0)
            return;
        for(File f : file.listFiles()) {
            String fileName = f.getName();
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(f);
            fileName = fileName.substring(0, fileName.length()-4);
            Game game = new Game(fileName);
            game.setStartSpawnLocation(Util.getStringLocation(configuration.getString("GameSettings.StartLocation"), true));
            game.setMinPlayers(configuration.getInt("GameSettings.MinPlayers"));
            game.setMaxPlayers(configuration.getInt("GameSettings.MaxPlayers"));
            game.setGoal(configuration.getInt("GameSettings.Goal"));
            game.setTime(configuration.getInt("GameSettings.Time"));
            List<Location> locations = new ArrayList<>();
            for(String s : configuration.getStringList("GameSettings.MobsSpawnsLocations")) {
                locations.add(Util.getStringLocation(s, true));
            }
            game.setMobsSpawnLocations(locations);
            game.startGame();
            games.add(game);
        }
    }

    public void joinGame(Player player, Game game) {
        if(game.getGameState() == GameState.RUNNING || game.getGameState() == GameState.ENDING || game.getGameState() == GameState.RESTARTING) {
            player.sendMessage(ChatColor.RED+"Game is still running!");
            return;
        }
        if(game.getPlayers().size() >= game.getMaxPlayers()) {
            player.sendMessage(ChatColor.RED+"This arena is full!");
            return;
        }
        if(game.getPlayers().containsKey(player)) {
            player.sendMessage(ChatColor.RED+"You are already in this arena!");
            return;
        }
        player.setScoreboard(game.getScoreboard());
        player.teleport(game.getStartSpawnLocation());
        player.getInventory().clear();
        player.getInventory().setItem(8, CustomItem.LEAVE.toItemStack());
        game.getPlayers().put(player, 0);
    }

    public void leaveGame(Player player, Game game) {
        if(!game.getPlayers().containsKey(player)) {
            player.sendMessage(ChatColor.RED+"You are not in game!");
            return;
        }
        game.getPlayers().remove(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        HunterGameMain.getInstance().getPlayerManager().sendToLobby(player);
        HunterGameMain.getInstance().getPlayerManager().loadLobby(player);
    }

    public void saveGames() {
        if(!games.isEmpty()) {
            games.stream().forEachOrdered(game -> {
                String fileName = game.getId()+".yml";
                File f = new File(HunterGameMain.getInstance().getDataFolder(), "arenas/"+fileName);
                if(!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(f);
                configuration.set("GameSettings.Goal", game.getGoal());
                configuration.set("GameSettings.Time", game.getTime());
                configuration.set("GameSettings.MinPlayers", game.getMinPlayers());
                configuration.set("GameSettings.MaxPlayers", game.getMaxPlayers());
                configuration.set("GameSettings.StartLocation", Util.getLocationString(game.getStartSpawnLocation(), true));
                List<String> locations = new ArrayList<>();
                for(Location l : game.getMobsSpawnLocations()) {
                    locations.add(Util.getLocationString(l, true));
                }
                configuration.set("GameSettings.MobsSpawnsLocations", locations);
                try {
                    configuration.save(f);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

}