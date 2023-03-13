package me.dreamdevs.github.huntergame.api.inventory;

import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;

public interface Action {

    void performAction(ClickInventoryEvent event);

}