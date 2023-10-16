package me.dreamdevs.github.huntergame.listeners;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.data.HGPlayer;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.game.GameState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class GameListeners implements Listener {

    @EventHandler
    public void killEntity(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) return;
        Player damager = event.getEntity().getKiller();
        HGPlayer hgPlayer = HunterGameMain.getInstance().getPlayerManager().getPlayer(damager);
        event.setDroppedExp(0);
        event.getDrops().clear();
        if(hgPlayer.isInGame()) {
            Game game = hgPlayer.getGame();
            if(game.getGameState() != GameState.RUNNING) return;

            Entity entity = event.getEntity();
            if(entity.getMetadata("points").isEmpty()) return;
            if(entity.getMetadata("points").get(0).asInt() > 0)
                game.updatePoints(hgPlayer.getPlayer(), entity.getMetadata("points").get(0).asInt());
            else
                game.updatePoints(hgPlayer.getPlayer(), -(game.getPlayers().get(damager)/2));
        }
    }

    //@EventHandler
    //public void damageEvent(EntityDamageByEntityEvent event) {
    //    if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
    //        if(gameType == GameType.CLASSIC || gameState == GameState.WAITING || gameState == GameState.ENDING || gameState == GameState.STARTING)
    //            event.setCancelled(true);
    //        else
    //        if(gameType == GameType.PVP) {
    //            if(((Player) event.getEntity()).getHealth() <= 4.0) {
    //                ((Player) event.getEntity()).setHealth(20);
    //                event.getEntity().teleport(startSpawnLocation);
    //            }
    //        }
    //    }
    //}
}