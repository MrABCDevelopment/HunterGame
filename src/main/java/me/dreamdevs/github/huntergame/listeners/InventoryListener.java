package me.dreamdevs.github.huntergame.listeners;

import lombok.Getter;
import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;
import me.dreamdevs.github.huntergame.api.inventory.GItem;
import me.dreamdevs.github.huntergame.api.inventory.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class InventoryListener implements Listener {

    public @Getter static HashMap<UUID, GUI> guis = new HashMap<>();

    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getHolder() instanceof GUI) {
            GUI gui = guis.get(event.getWhoClicked().getUniqueId());
            event.setResult(Event.Result.DENY);
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            if (slot >= 0 && slot < gui.getSize() && slot < gui.getItemStacks().length) {
                if (gui.getItemStacks()[slot] == null) return;
                GItem gItem = gui.getItemStacks()[slot];
                ClickInventoryEvent clickInventoryEvent = new ClickInventoryEvent(event, player, gui, slot, gItem, event.getClick());
                Bukkit.getPluginManager().callEvent(clickInventoryEvent);
                gItem.execute(clickInventoryEvent);
            }
        }
    }

    @EventHandler
    public void closeEvent(InventoryCloseEvent event) {
        if(event.getInventory().getHolder() instanceof GUI) {
            guis.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        guis.remove(event.getPlayer().getUniqueId());
    }

}