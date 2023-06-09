package me.dreamdevs.github.huntergame.api.inventory;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.listeners.InventoryListener;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class GUI implements InventoryHolder {

    private Inventory inventory;
    private String title;
    private int size;

    private GItem[] itemStacks;

    public GUI(String title, GUISize guiSize) {
        this.title = ColourUtil.colorize(title);
        this.size = guiSize.getSize();
        this.itemStacks = new GItem[size];
        this.inventory = Bukkit.createInventory(this, size, this.title);
    }

    public void openGUI(Player player) {
        InventoryListener.guis.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public void setItem(int slot, GItem gItem) {
        this.itemStacks[slot] = gItem;
        inventory.setItem(slot, gItem.getItemStack());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}