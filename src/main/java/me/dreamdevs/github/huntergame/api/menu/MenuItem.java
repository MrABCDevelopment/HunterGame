package me.dreamdevs.github.huntergame.api.menu;

import lombok.Getter;
import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    private String displayName;
    private List<String> lore;
    private Material material;
    private @Getter ItemStack itemStack;
    private ItemAction itemAction;

    public MenuItem name(String displayName) {
        this.displayName = ColourUtil.colorize(displayName);
        return this;
    }

    public MenuItem material(Material material) {
        this.material = material;
        return this;
    }

    public MenuItem lore(String... lore) {
        this.lore = ColourUtil.colouredLore(lore);
        return this;
    }

    public MenuItem lore(List<String> lore) {
        this.lore = ColourUtil.colouredLore(lore);
        return this;
    }

    public MenuItem action(ItemAction action) {
        this.itemAction = action;
        return this;
    }

    public MenuItem build() {
        try {
            this.itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(displayName != null)
                itemMeta.setDisplayName(displayName);
            if(lore != null)
                itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        } catch (Exception e) {
            Util.sendPluginMessage("&cCouldn't build menu item, because there's missing material!");
        }
        return this;
    }

    public void performAction(ClickInventoryEvent event) {
        if(itemAction != null)
            this.itemAction.action(event);
    }

}