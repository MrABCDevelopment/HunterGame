package me.dreamdevs.github.huntergame.api.events;

import lombok.Getter;
import me.dreamdevs.github.huntergame.api.inventory.GItem;
import me.dreamdevs.github.huntergame.api.inventory.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

@Getter
public class ClickInventoryEvent extends Event {

    private static final HandlerList list = new HandlerList();

    private InventoryClickEvent event;
    private Player player;
    private int slot;
    private GUI gui;
    private GItem gItem;
    private ClickType clickType;

    public ClickInventoryEvent(InventoryClickEvent event, Player player, GUI gui, int slot, GItem gItem, ClickType clickType) {
        this.event = event;
        this.player = player;
        this.gui = gui;
        this.slot = slot;
        this.gItem = gItem;
        this.clickType = clickType;
    }

    public static HandlerList getHandlerList() {
        return list;
    }
    @Override
    public HandlerList getHandlers() {
        return list;
    }
}