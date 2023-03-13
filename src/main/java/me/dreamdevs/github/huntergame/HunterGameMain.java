package me.dreamdevs.github.huntergame;

import lombok.Getter;
import me.dreamdevs.github.huntergame.commands.CommandHandler;
import me.dreamdevs.github.huntergame.listeners.InventoryListener;
import me.dreamdevs.github.huntergame.listeners.PlayerInteractListener;
import me.dreamdevs.github.huntergame.listeners.PlayerListeners;
import me.dreamdevs.github.huntergame.managers.CooldownManager;
import me.dreamdevs.github.huntergame.managers.GameManager;
import me.dreamdevs.github.huntergame.managers.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class HunterGameMain extends JavaPlugin {

    private @Getter static HunterGameMain instance;
    private PlayerManager playerManager;
    private CooldownManager cooldownManager;
    private GameManager gameManager;
    private Lobby lobby;

    public static List<String> MESSAGE;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveConfig();
        reloadConfig();

        MESSAGE = getConfig().getStringList("items.how-to-play.Message");

        this.lobby = new Lobby();
        this.cooldownManager = new CooldownManager();
        this.playerManager = new PlayerManager();
        this.gameManager = new GameManager();

        new CommandHandler(this);

        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        getGameManager().saveGames();
        getLobby().saveLobby();
        saveConfig();
    }
}