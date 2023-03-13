package me.dreamdevs.github.huntergame.api.inventory.actions;

import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;
import me.dreamdevs.github.huntergame.api.inventory.Action;
import me.dreamdevs.github.huntergame.api.inventory.GUI;

public class OpenGUIAction implements Action {

    private GUI gui;

    public OpenGUIAction(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void performAction(ClickInventoryEvent event) {
        event.getEvent().getWhoClicked().closeInventory();
        gui.openGUI(event.getPlayer());
    }
}