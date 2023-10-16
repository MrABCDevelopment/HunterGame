package me.dreamdevs.github.huntergame.utils;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
public enum CustomItem {

    ARENA_SELECTOR(Material.CHEST, HunterGameMain.getInstance().getConfigManager().getConfig("items.yml").getString("items.arena-selector.DisplayName"),
            HunterGameMain.getInstance().getConfigManager().getConfig("items.yml").getStringList("items.arena-selector.Lore")),
    MY_PROFILE(Material.PLAYER_HEAD, HunterGameMain.getInstance().getConfigManager().getConfig("items.yml").getString("items.profile.DisplayName"),
            HunterGameMain.getInstance().getConfigManager().getConfig("items.yml").getStringList("items.profile.Lore")),


    LEAVE(Material.RED_BED, HunterGameMain.getInstance().getConfigManager().getConfig("items.yml").getString("items.leave.DisplayName"),
            HunterGameMain.getInstance().getConfigManager().getConfig("items.yml").getStringList("items.leave.Lore"));

    private final String displayName;
    private final Material material;
    private final List<String> lore;

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