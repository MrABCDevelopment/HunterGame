package me.dreamdevs.github.huntergame.api.events;

import lombok.Getter;
import me.dreamdevs.github.huntergame.api.menu.Menu;
import me.dreamdevs.github.huntergame.api.menu.MenuItem;
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
    private Menu menu;
    private MenuItem menuItem;
    private ClickType clickType;

    public ClickInventoryEvent(InventoryClickEvent event, Player player, Menu menu, int slot, MenuItem menuItem, ClickType clickType) {
        this.event = event;
        this.player = player;
        this.menu = menu;
        this.slot = slot;
        this.menuItem = menuItem;
        this.clickType = clickType;
    }

    @Override
    public HandlerList getHandlers() {
        return list;
    }
}