package me.dreamdevs.github.huntergame.listeners;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.menu.Menu;
import me.dreamdevs.github.huntergame.api.menu.MenuItem;
import me.dreamdevs.github.huntergame.data.HGPlayer;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import me.dreamdevs.github.huntergame.utils.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void interactEvent(PlayerInteractEvent event) {
        if(event.getItem() == null)
            return;
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemStack = event.getItem();
            Player player = event.getPlayer();
            HGPlayer hgPlayer = HunterGameMain.getInstance().getPlayerManager().getPlayer(player);
            if (itemStack.getItemMeta().getDisplayName().equals(CustomItem.INFO_BOOK.getDisplayName()) && itemStack.getItemMeta().getLore().equals(CustomItem.INFO_BOOK.getLore())) {
                event.setCancelled(true);
                if (HunterGameMain.getInstance().getCooldownManager().isOnCooldown(player.getUniqueId(), "infobook")) {
                    return;
                }
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, (float) Math.random());

                // HunterGameMain.MESSAGE.stream().map(ColourUtil::colorize).forEach(player::sendMessage);
                HunterGameMain.getInstance().getCooldownManager().setCooldown(player.getUniqueId(), "infobook", 3);
            }
            if (itemStack.getItemMeta().getDisplayName().equals(CustomItem.ARENA_SELECTOR.getDisplayName()) && itemStack.getItemMeta().getLore().equals(CustomItem.ARENA_SELECTOR.getLore())) {
                event.setCancelled(true);
                if (HunterGameMain.getInstance().getCooldownManager().isOnCooldown(player.getUniqueId(), "arenaselector")) {
                    return;
                }
                if(HunterGameMain.getInstance().getGameManager().getGames().isEmpty()) {
                    player.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("no-arenas"));
                    return;
                }
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, (float) Math.random());
                Menu menu = new Menu(HunterGameMain.getInstance().getConfigManager().getConfig("items.yml").getString("items.arena-selector.DisplayName"), 6);
                HunterGameMain.getInstance().getGameManager().getGames().forEach(game -> {
                    MenuItem menuItem = new MenuItem().material(Material.GRASS_BLOCK).name("&aArena "+game.getId())
                            .lore("", "&7Players: &b"+game.getPlayers().size()+"/"+game.getMaxPlayers(),
                                    "&7Status: &b"+game.getGameState().name(),
                                    "",
                                    "&7Click to join to the arena.")
                            .action(itemAction -> {
                                itemAction.getPlayer().closeInventory();
                                HunterGameMain.getInstance().getGameManager().joinGame(player, game);
                            });
                    menu.addItem(menuItem);
                });
                menu.open(player);
                HunterGameMain.getInstance().getCooldownManager().setCooldown(player.getUniqueId(), "arenaselector", 3);
            }
            if (itemStack.getItemMeta().getDisplayName().equals(CustomItem.LEAVE.getDisplayName()) && itemStack.getItemMeta().getLore().equals(CustomItem.LEAVE.getLore())) {
                event.setCancelled(true);
                if (HunterGameMain.getInstance().getCooldownManager().isOnCooldown(player.getUniqueId(), "leave")) {
                    return;
                }
                Game game = hgPlayer.getGame();
                HunterGameMain.getInstance().getGameManager().leaveGame(player, game);
                HunterGameMain.getInstance().getCooldownManager().setCooldown(player.getUniqueId(), "leave", 3);
            }
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if(event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
            return;
        event.setResult(Event.Result.DENY);
    }

    @EventHandler
    public void inventoryClick(InventoryDragEvent event) {
        if(event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
            return;
        event.setResult(Event.Result.DENY);
    }

}
