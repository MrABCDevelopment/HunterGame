package me.dreamdevs.github.huntergame.api.inventory;

import lombok.Getter;
import lombok.Setter;
import me.dreamdevs.github.huntergame.api.events.ClickInventoryEvent;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter @Setter
public class GItem {

    private ItemStack itemStack;
    private Material material;
    private int amount;
    private List<Action> actions;
    private String displayName;
    private Map<Enchantment, Integer> enchantments;
    private Set<ItemFlag> itemFlags;
    private boolean unbreakable;
    private List<String> lore;
    private ItemMeta itemMeta;

    public GItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.actions = new ArrayList<>();
    }

    public GItem(Material material, String displayName, List<String> lore, int amount, Map<Enchantment, Integer> enchantments, Set<ItemFlag> itemFlags, boolean unbreakable, Action... actions) {
        this.material = material;
        this.displayName = ColourUtil.colorize(displayName);
        this.lore = ColourUtil.colouredLore(lore);
        this.amount = amount;
        this.enchantments = enchantments;
        this.itemFlags = itemFlags;
        this.unbreakable = unbreakable;
        this.actions = new ArrayList<>();
        this.actions.addAll(Collections.unmodifiableList(Arrays.asList(actions)));
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName(this.displayName);
        this.itemMeta.setLore(this.lore);
        for(ItemFlag flag : this.itemFlags)
            this.itemMeta.addItemFlags(flag);
        this.itemMeta.setUnbreakable(this.unbreakable);
        this.itemStack.setItemMeta(this.itemMeta);
        this.itemStack.setAmount(this.amount);
        this.itemStack.addUnsafeEnchantments(this.enchantments);
    }

    public GItem(Material material, int amount, Action... action) {
        this(material, null, new ArrayList<>(), amount, new HashMap<>(), new HashSet<>(), false, action);
    }

    public GItem(Material material, int amount) {
        this(material, null, new ArrayList<>(), amount, new HashMap<>(), new HashSet<>(), false,  new Action[0]);
    }

    public GItem(Material material) {
        this(material, material.name(), new ArrayList<>(), 1, new HashMap<>(), new HashSet<>(), false, new Action[0]);
    }

    public GItem(Material material, String displayName, List<String> list) {
        this(material, displayName, list, 1, new HashMap<>(), new HashSet<>(), false, new Action[0]);
    }

    public static GItem toGItem(ItemStack itemStack) {
        return new GItem(itemStack.getType());
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void addActions(Action... actions) {
        for(Action action : actions) {
            addAction(action);
        }
    }

    public void execute(ClickInventoryEvent event) {
        for(Action action : actions)
            action.performAction(event);
    }

}