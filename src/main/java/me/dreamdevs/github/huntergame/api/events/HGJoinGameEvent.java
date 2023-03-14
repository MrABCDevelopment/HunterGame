package me.dreamdevs.github.huntergame.api.events;

import lombok.Getter;
import me.dreamdevs.github.huntergame.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class HGJoinGameEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private Player player;
    private Game game;

    public HGJoinGameEvent(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}