package me.dreamdevs.github.huntergame.api.inventory.actions;

import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;
import me.dreamdevs.github.huntergame.api.inventory.Action;

public class CloseAction implements Action {

    @Override
    public void performAction(ClickInventoryEvent event) {
        event.getPlayer().closeInventory();
    }
}