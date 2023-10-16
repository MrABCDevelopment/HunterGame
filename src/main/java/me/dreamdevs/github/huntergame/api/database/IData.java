package me.dreamdevs.github.huntergame.api.database;

import me.dreamdevs.github.huntergame.game.GamePlayer;

public interface IData {

    void connectDatabase();

    void disconnectDatabase();

    void saveAllStatistics(GamePlayer gamePlayer);

    void loadAllStatistics(GamePlayer gamePlayer);

}