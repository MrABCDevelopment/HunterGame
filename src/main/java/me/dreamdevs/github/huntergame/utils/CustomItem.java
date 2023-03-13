package me.dreamdevs.github.huntergame.utils;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
public enum CustomItem {

    ARENA_SELECTOR(Material.CHEST, HunterGameMain.getInstance().getConfig().getString("items.arena-selector.DisplayName"),
            HunterGameMain.getInstance().getConfig().getStringList("items.arena-selector.Lore")),
    INFO_BOOK(Material.BOOK, HunterGameMain.getInstance().getConfig().getString("items.how-to-play.DisplayName"),
            HunterGameMain.getInstance().getConfig().getStringList("items.how-to-play.Lore")),

    // STATS(Material.FEATHER, HunterGameMain.getInstance().getConfig().getString("items.stats.DisplayName"),
    //        HunterGameMain.getInstance().getConfig().getStringList("items.stats.Lore")),
    LEAVE(Material.RED_BED, HunterGameMain.getInstance().getConfig().getString("items.leave.DisplayName"),
            HunterGameMain.getInstance().getConfig().getStringList("items.leave.Lore"));

    private String displayName;
    private Material material;
    private List<String> lore;

    CustomItem(Material material, String displayName, List<String> lore) {
        this.material = material;
        this.displayName = ColourUtil.colorize(displayName);
        this.lore = ColourUtil.colouredLore(lore);
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}