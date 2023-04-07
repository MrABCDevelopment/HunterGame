package me.dreamdevs.github.huntergame.managers;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.events.HGJoinGameEvent;
import me.dreamdevs.github.huntergame.api.events.HGLeaveGameEvent;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.game.GameState;
import me.dreamdevs.github.huntergame.game.GameType;
import me.dreamdevs.github.huntergame.utils.CustomItem;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
            loadGame(f);
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
        player.setScoreboard(game.getHunterBoard().getScoreboard());
        player.teleport(game.getStartSpawnLocation());
        player.getInventory().clear();
        player.getInventory().setItem(8, CustomItem.LEAVE.toItemStack());
        game.getPlayers().put(player, 0);
        HGJoinGameEvent event = new HGJoinGameEvent(player, game);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void leaveGame(Player player, Game game) {
        if(!game.getPlayers().containsKey(player)) {
            player.sendMessage(ChatColor.RED+"You are not in game!");
            return;
        }
        HunterGameMain.getInstance().getPlayerManager().sendToLobby(player);
        HunterGameMain.getInstance().getPlayerManager().loadLobby(player);
        game.getPlayers().remove(player);
        HGLeaveGameEvent event = new HGLeaveGameEvent(player, game);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void stop(Game game) {
        game.cancel();
        games.remove(game);
    }

    public void loadGame(File file) {
        Util.createFile(file);
        String fileName = file.getName();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        fileName = fileName.substring(0, fileName.length()-4);
        Game game = new Game(fileName);
        game.setFile(file);
        game.setStartSpawnLocation(Util.getStringLocation(configuration.getString("GameSettings.StartLocation"), true));
        game.setMinPlayers(configuration.getInt("GameSettings.MinPlayers"));
        game.setMaxPlayers(configuration.getInt("GameSettings.MaxPlayers"));
        game.setGoal(configuration.getInt("GameSettings.Goal"));
        game.setGameTime(configuration.getInt("GameSettings.Time"));
        game.setSpawnMobsTime(configuration.getInt("GameSettings.SpawnMobsTime"));
        game.setGameType(GameType.valueOf(configuration.getString("GameSettings.Type").toUpperCase()));
        Map<Location, String> map = new HashMap<>();
        for(String s : configuration.getStringList("GameSettings.MobsSpawnsLocations")) {
            String[] strings = s.split(";");
            map.put(Util.getStringLocation(strings[1], true), strings[0]);
        }
        game.setMobsLocations(map);
        game.startGame();
        games.add(game);
    }

    public void saveGame(Game game) {
        String fileName = game.getId()+".yml";
        File f = new File(HunterGameMain.getInstance().getDataFolder(), "arenas/"+fileName);
        Util.createFile(f);

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(f);
        configuration.set("GameSettings.Goal", game.getGoal());
        configuration.set("GameSettings.Time", game.getGameTime());
        configuration.set("GameSettings.MinPlayers", game.getMinPlayers());
        configuration.set("GameSettings.MaxPlayers", game.getMaxPlayers());
        configuration.set("GameSettings.SpawnMobsTime", game.getSpawnMobsTime());
        configuration.set("GameSettings.StartLocation", Util.getLocationString(game.getStartSpawnLocation(), true));
        configuration.set("GameSettings.Type", game.getGameType().name());
        List<String> locations = new ArrayList<>();
        for(Map.Entry<Location, String> maps : game.getMobsLocations().entrySet()) {
            String line = maps.getValue()+";"+Util.getLocationString(maps.getKey(), true);
            locations.add(line);
        }
        configuration.set("GameSettings.MobsSpawnsLocations", locations);
        try {
            configuration.save(f);
        } catch (IOException e) { }
    }

    public void saveGames() {
        if(!games.isEmpty()) {
            games.forEach(this::saveGame);
        }
    }

}