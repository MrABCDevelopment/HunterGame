package me.dreamdevs.github.huntergame;

import lombok.Getter;
import me.dreamdevs.github.huntergame.commands.CommandHandler;
import me.dreamdevs.github.huntergame.game.Lobby;
import me.dreamdevs.github.huntergame.listeners.InventoryListener;
import me.dreamdevs.github.huntergame.listeners.PlayerInteractListener;
import me.dreamdevs.github.huntergame.listeners.PlayerListeners;
import me.dreamdevs.github.huntergame.managers.*;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class HunterGameMain extends JavaPlugin {

    private @Getter static HunterGameMain instance;
    private PlayerManager playerManager;
    private CooldownManager cooldownManager;
    private GameManager gameManager;
    private Lobby lobby;
    private MessagesManager messagesManager;

    public static List<String> MESSAGE;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveConfig();
        reloadConfig();

        MESSAGE = getConfig().getStringList("items.how-to-play.Message");

        this.lobby = new Lobby();
        this.messagesManager = new MessagesManager(this);
        this.cooldownManager = new CooldownManager();
        this.playerManager = new PlayerManager();
        this.gameManager = new GameManager();

        new CommandHandler(this);

        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        if(getConfig().getBoolean("update-checker")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> new UpdateChecker(HunterGameMain.getInstance(), 108629).getVersion(version -> {
                if (getDescription().getVersion().equals(version)) {
                    Util.sendPluginMessage("");
                    Util.sendPluginMessage("&aThere is new HunterGame version!");
                    Util.sendPluginMessage("&aYour version: " + getDescription().getVersion());
                    Util.sendPluginMessage("&aNew version: " + version);
                    Util.sendPluginMessage("");
                } else {
                    Util.sendPluginMessage("");
                    Util.sendPluginMessage("&aYour version is up to date!");
                    Util.sendPluginMessage("&aYour version: " + getDescription().getVersion());
                    Util.sendPluginMessage("");
                }
            }), 10L, 20 * 600);
        }
    }

    @Override
    public void onDisable() {
        getGameManager().saveGames();
        getLobby().saveLobby();
        saveConfig();
    }
}