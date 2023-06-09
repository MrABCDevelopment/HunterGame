package me.dreamdevs.github.huntergame.managers;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.utils.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerManager {

    public void loadLobby(Player player) {
        player.getInventory().setItem(4, CustomItem.ARENA_SELECTOR.toItemStack());
        player.getInventory().setItem(0, CustomItem.INFO_BOOK.toItemStack());
    }

    public void sendToLobby(Player player) {
        HunterGameMain.getInstance().getLobby().teleportPlayerToLobby(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setFoodLevel(20);
        player.setBedSpawnLocation(null);
        player.setExp(0);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
    }

}