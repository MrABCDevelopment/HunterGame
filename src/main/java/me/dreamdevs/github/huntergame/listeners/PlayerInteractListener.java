package me.dreamdevs.github.huntergame.listeners;

import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.inventory.GItem;
import me.dreamdevs.github.huntergame.api.inventory.GUI;
import me.dreamdevs.github.huntergame.api.inventory.GUISize;
import me.dreamdevs.github.huntergame.api.inventory.actions.CloseAction;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import me.dreamdevs.github.huntergame.utils.CustomItem;
import org.bukkit.ChatColor;
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

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void interactEvent(PlayerInteractEvent event) {
        if(event.getItem() == null)
            return;
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemStack = event.getItem();
            Player player = event.getPlayer();
            if (itemStack.getItemMeta().getDisplayName().equals(CustomItem.INFO_BOOK.getDisplayName()) && itemStack.getItemMeta().getLore().equals(CustomItem.INFO_BOOK.getLore())) {
                event.setCancelled(true);
                if (HunterGameMain.getInstance().getCooldownManager().isOnCooldown(player.getUniqueId(), "infobook")) {
                    return;
                }
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, (float) Math.random());
                HunterGameMain.MESSAGE.stream().map(ColourUtil::colorize).forEach(player::sendMessage);
                HunterGameMain.getInstance().getCooldownManager().setCooldown(player.getUniqueId(), "infobook", 3);
            }
            if (itemStack.getItemMeta().getDisplayName().equals(CustomItem.ARENA_SELECTOR.getDisplayName()) && itemStack.getItemMeta().getLore().equals(CustomItem.ARENA_SELECTOR.getLore())) {
                event.setCancelled(true);
                if (HunterGameMain.getInstance().getCooldownManager().isOnCooldown(player.getUniqueId(), "arenaselector")) {
                    return;
                }
                if(HunterGameMain.getInstance().getGameManager().getGames().isEmpty()) {
                    player.sendMessage(ChatColor.RED+"There are no arenas!");
                    return;
                }
                GUI gui = new GUI(HunterGameMain.getInstance().getConfig().getString("items.arena-selector.DisplayName"), GUISize.SIX_ROWS);
                AtomicInteger atomicInteger = new AtomicInteger(0);
                HunterGameMain.getInstance().getGameManager().getGames().forEach(game -> {
                    GItem gItem = new GItem(Material.GRASS_BLOCK, ColourUtil.colorize("&aArena "+game.getId()), ColourUtil.colouredLore("", "&7Players: &b"+game.getPlayers().size()+"/"+game.getMaxPlayers(), "&7Status: &b"+game.getGameState().name(), "&7Type: &b"+game.getGameType().name()));
                    gItem.addActions(new CloseAction(), actionEvent -> HunterGameMain.getInstance().getGameManager().joinGame(player, game));
                    gui.setItem(atomicInteger.getAndIncrement(), gItem);
                });
                gui.openGUI(player);
                HunterGameMain.getInstance().getCooldownManager().setCooldown(player.getUniqueId(), "arenaselector", 3);
            }
            if (itemStack.getItemMeta().getDisplayName().equals(CustomItem.LEAVE.getDisplayName()) && itemStack.getItemMeta().getLore().equals(CustomItem.LEAVE.getLore())) {
                event.setCancelled(true);
                if (HunterGameMain.getInstance().getCooldownManager().isOnCooldown(player.getUniqueId(), "leave")) {
                    return;
                }
                Game game = HunterGameMain.getInstance().getGameManager().getGames().stream().filter(g -> g.getPlayers().containsKey(player)).findFirst().get();
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
