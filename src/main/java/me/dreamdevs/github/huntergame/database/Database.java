package me.dreamdevs.github.huntergame.database;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.database.IData;
import me.dreamdevs.github.huntergame.game.GamePlayer;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Bukkit;

public class Database {

    private @Getter IData iData;

    public void connect(String databaseType) {
        Class<? extends IData> database = null;
        Util.sendPluginMessage("&aConnecting to database...");
        try {
            database = Class.forName("me.dreamdevs.github.huntergame.database.Database" + databaseType).asSubclass(IData.class);
            iData = database.newInstance();
            iData.connectDatabase();
            Util.sendPluginMessage("&aConnected to "+databaseType+" database.");
        } catch (Exception e) {
            Util.sendPluginMessage("&cDatabase type '"+databaseType+"' does not exist!");
        }
    }

    public void disconnect() {
        iData.disconnectDatabase();
        Util.sendPluginMessage("&aDisconnected from the database.");
    }

    public void autoSaveData() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(HunterGameMain.getInstance(), () -> HunterGameMain.getInstance().getPlayerManager().getPlayers().forEach(this::saveData), 0L, 20*300L);
        Util.sendPluginMessage("&aData saved!");
    }

    public void saveData(GamePlayer gamePlayer) {
        iData.saveAllStatistics(gamePlayer);
    }

    public void loadData(GamePlayer gamePlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(HunterGameMain.getInstance(), () -> iData.loadAllStatistics(gamePlayer));
    }

}