package me.dreamdevs.github.huntergame.managers;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.events.HGJoinGameEvent;
import me.dreamdevs.github.huntergame.api.events.HGLeaveGameEvent;
import me.dreamdevs.github.huntergame.data.HGPlayer;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.game.GameState;
import me.dreamdevs.github.huntergame.utils.CustomItem;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Bukkit;
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
        this.games = new ArrayList<>();
        File file = new File(HunterGameMain.getInstance().getDataFolder(), "arenas");
        if(!file.exists() || !file.isDirectory())
            file.mkdirs();

        Optional.ofNullable(file.listFiles(((dir, name) -> name.endsWith(".yml")))).ifPresent(files -> Arrays.asList(files).forEach(this::loadGame));
    }

    public void joinGame(Player player, Game game) {
        if(game.getGameState() != GameState.WAITING || game.getGameState() != GameState.STARTING) {
            player.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("game-running"));
            return;
        }
        if(game.getPlayers().size() >= game.getMaxPlayers()) {
            player.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("game-full"));
            return;
        }
        HGPlayer hgPlayer = HunterGameMain.getInstance().getPlayerManager().getPlayer(player);
        if(hgPlayer.isInGame()) {
            player.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("player-ingame"));
            return;
        }
        player.teleport(game.getStartSpawnLocation());
        hgPlayer.clearInventory();
        player.getInventory().setItem(8, CustomItem.LEAVE.toItemStack());
        game.getPlayers().put(player, 0);
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            player.hidePlayer(HunterGameMain.getInstance(), onlinePlayer);
            onlinePlayer.hidePlayer(HunterGameMain.getInstance(), player);
        });

        game.getPlayers().keySet().forEach(gamePlayer -> {
            player.showPlayer(HunterGameMain.getInstance(), gamePlayer);
            gamePlayer.showPlayer(HunterGameMain.getInstance(), player);
        });
        HGJoinGameEvent event = new HGJoinGameEvent(player, game);
        Bukkit.getPluginManager().callEvent(event);
        if(game.getPlayers().size() >= game.getMinPlayers()) {
            game.setGameState(GameState.STARTING);
            game.setTimer(30);
        }
    }

    public void leaveGame(Player player, Game game) {
        HGPlayer hgPlayer = HunterGameMain.getInstance().getPlayerManager().getPlayer(player);
        if(!hgPlayer.isInGame()) {
            player.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("not-ingame"));
            return;
        }
        HunterGameMain.getInstance().getPlayerManager().sendToLobby(player);
        HunterGameMain.getInstance().getPlayerManager().loadLobby(player);
        game.getPlayers().remove(player);
        HGLeaveGameEvent event = new HGLeaveGameEvent(player, game);
        Bukkit.getPluginManager().callEvent(event);
        if(game.getPlayers().size() < game.getMinPlayers()) {
            game.setGameState(GameState.WAITING);
            game.setTimer(0);
        }
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
        List<Location> locations = new ArrayList<>();
        for(String s : configuration.getStringList("GameSettings.MobsSpawnsLocations"))
            locations.add(Util.getStringLocation(s, true));
        game.setMobsLocations(locations);
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
        //configuration.set("GameSettings.Type", game.getGameType().name());
        List<String> locations = new ArrayList<>();
        for(Location location : game.getMobsLocations()) {
            String line = Util.getLocationString(location, true);
            locations.add(line);
        }
        configuration.set("GameSettings.MobsSpawnsLocations", locations);
        try {
            configuration.save(f);
        } catch (IOException e) { }
    }

    public void saveGames() {
        Optional.ofNullable(games).ifPresent(games1 -> games1.forEach(this::saveGame));
    }

    public void forceRemovePlayer(Player player) {
        games.stream().filter(game -> game.getPlayers().containsKey(player)).forEach(game -> game.getPlayers().remove(player));
        Util.sendPluginMessage("&cPlayer "+player.getName()+" was removed from the game, because he left.");
    }

}