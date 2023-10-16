package me.dreamdevs.github.huntergame.api.menu;

import lombok.Getter;
import me.dreamdevs.github.huntergame.listeners.InventoryListener;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;

@Getter
public class Menu implements InventoryHolder {

    private final String title;
    private final int rows;
    private final Inventory inventory;
    private MenuItem[] menuItems;

    public Menu(String title, int rows) {
        this.title = ColourUtil.colorize(title);
        this.rows = rows;
        if(rows > 6 || rows < 1) {
            rows = 3;
        }

        this.menuItems = new MenuItem[rows*9];
        this.inventory = Bukkit.createInventory(this, rows*9, this.title);
    }

    public void fillPanels() {
        Arrays.fill(menuItems, new MenuItem().material(Material.WHITE_STAINED_GLASS_PANE).name("&c&l*").build());
    }

    public void fill() {
        for(int x = 0; x<inventory.getSize(); x++) {
            setItem(x, new MenuItem().material(Material.WHITE_STAINED_GLASS_PANE).name("&c&l*").build());
        }
    }

    public void setItem(int slot, MenuItem menuItem) {
        this.menuItems[slot] = menuItem;
    }

    public void addItem(MenuItem menuItem) {
        for(int x = 0; x<menuItems.length; x++) {
            if(menuItems[x] == null) {
                menuItems[x] = menuItem;
                break;
            }
        }
    }

    public void open(Player player) {
        inventory.clear();

        for(int x = 0; x<menuItems.length; x++) {
            if(menuItems[x] != null)
                inventory.setItem(x, menuItems[x].getItemStack());
        }

        InventoryListener.menus.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}