package me.dreamdevs.github.huntergame;

import lombok.Getter;
import lombok.Setter;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

@Getter @Setter
public class Lobby {

    private Location lobbyLocation;
    private File lobbyFile;
    private FileConfiguration configuration;

    public Lobby() {
        lobbyFile = new File(HunterGameMain.getInstance().getDataFolder(), "lobby.yml");
        try {
            lobbyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Util.sendPluginMessage("&cSomething went wrong while creating a lobby.yml file.");
        }
        configuration = YamlConfiguration.loadConfiguration(lobbyFile);
        if(configuration.isConfigurationSection("lobby")) {
            lobbyLocation = Util.getStringLocation(configuration.getConfigurationSection("lobby").getString("location"), true);
        }
    }

    public void teleportPlayerToLobby(Player player) {
        if(lobbyLocation != null)
            player.teleport(lobbyLocation);
        else
            player.sendMessage(ColourUtil.colorize("&cLobby is not set!"));
    }

    public void saveLobby() {
        try {
            configuration.set("lobby.location", Util.getLocationString(lobbyLocation, true));
            configuration.save(lobbyFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}