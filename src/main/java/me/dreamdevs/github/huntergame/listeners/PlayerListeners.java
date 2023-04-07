package me.dreamdevs.github.huntergame.listeners;

import me.dreamdevs.github.huntergame.HunterGameMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListeners implements Listener {

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        HunterGameMain.getInstance().getPlayerManager().sendToLobby(event.getPlayer());
        HunterGameMain.getInstance().getPlayerManager().loadLobby(event.getPlayer());
    }

    @EventHandler
    public void foodChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void dropEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}