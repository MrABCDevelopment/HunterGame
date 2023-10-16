package me.dreamdevs.github.huntergame.api.menu;

import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;

public interface ItemAction {

    void action(ClickInventoryEvent event);

}