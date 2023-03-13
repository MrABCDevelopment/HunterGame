package me.dreamdevs.github.huntergame.api.inventory.actions;

import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;
import me.dreamdevs.github.huntergame.api.inventory.Action;
import org.bukkit.Sound;

public class PlaySoundAction implements Action {

    private Sound sound;
    private float volume;
    private float pitch;

    public PlaySoundAction(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void performAction(ClickInventoryEvent event) {
        event.getPlayer().playSound(event.getPlayer().getLocation(), sound, volume, pitch);
    }

}