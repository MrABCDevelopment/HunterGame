package me.dreamdevs.github.huntergame;

import lombok.Getter;
import me.dreamdevs.github.huntergame.commands.CommandHandler;
import me.dreamdevs.github.huntergame.database.Database;
import me.dreamdevs.github.huntergame.game.Lobby;
import me.dreamdevs.github.huntergame.listeners.GameListeners;
import me.dreamdevs.github.huntergame.listeners.InventoryListener;
import me.dreamdevs.github.huntergame.listeners.PlayerInteractListener;
import me.dreamdevs.github.huntergame.listeners.PlayerListeners;
import me.dreamdevs.github.huntergame.managers.*;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class HunterGameMain extends JavaPlugin {

    private @Getter static HunterGameMain instance;
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private PlayerManager playerManager;
    private Database database;
    private GameManager gameManager;
    private CooldownManager cooldownManager;
    private Lobby lobby;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.configManager.loadConfigFiles("messages.yml", "levels.yml", "items.yml", "scoreboards.yml");
        this.cooldownManager = new CooldownManager();

        this.messagesManager = new MessagesManager(this);

        this.database = new Database();
        this.database.connect("YAML");
        this.database.autoSaveData();

        this.lobby = new Lobby();

        this.playerManager = new PlayerManager();

        this.gameManager = new GameManager();

        new CommandHandler(this);

        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new GameListeners(), this);

        if(getConfig().getBoolean("update-checker")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> new UpdateChecker(HunterGameMain.getInstance(), 108629).getVersion(version -> {
                if (getDescription().getVersion().equals(version)) {
                    Util.sendPluginMessage("");
                    Util.sendPluginMessage("&aYour version is up to date!");
                    Util.sendPluginMessage("&aYour version: " + getDescription().getVersion());
                    Util.sendPluginMessage("");
                } else {
                    Util.sendPluginMessage("");
                    Util.sendPluginMessage("&aThere is new HunterGame version!");
                    Util.sendPluginMessage("&aYour version: " + getDescription().getVersion());
                    Util.sendPluginMessage("&aNew version: " + version);
                    Util.sendPluginMessage("");
                }
            }), 10L, 20 * 600);
        }

        Util.sendPluginMessage(getConfigManager().getConfig("items.yml").getString("items.how-to-play.Message"));
    }

    @Override
    public void onDisable() {
        this.database.disconnect();

        getGameManager().saveGames();
        //getLobby().saveLobby();
    }
}