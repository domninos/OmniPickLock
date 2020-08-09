package net.omni.picklock;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class PickLock {
    private final PickLockPlugin plugin;
    private final int tier;
    private int chance;
    private Material material;
    private String name;
    private List<String> lore;
    private ItemStack picklock;

    public PickLock(PickLockPlugin plugin, int tier) {
        this.plugin = plugin;
        this.tier = tier;

        if (tier < 0 || tier > 3) {
            plugin.sendConsole("&cTier: " + tier + " is not a valid tier.");
            return;
        }

        String path = "picklock." + tier + ".";

        this.chance = plugin.getConfig().getInt(path + "chance");

        Material material = Material.getMaterial(plugin.getConfig().getString(path + "type"));

        if (material == null) {
            plugin.sendConsole("&cCould not find lockpick material.");
            return;
        }

        this.material = material;
        this.name = plugin.translate(plugin.getConfig().getString(path + "name"));

        List<String> lore = plugin.getConfig().getStringList(path + "lore");

        if (lore == null || lore.isEmpty()) {
            plugin.sendConsole("&cLore not found.");
            return;
        }

        this.lore = lore;

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(plugin.translate(name + " Tier " + tier));

        lore.add("&aChance: &2" + chance);
        lore.add(" ");
        meta.setLore(lore.stream().map(l -> plugin.translate(l)).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        this.picklock = itemStack;

        plugin.sendConsole("&aLoaded picklock tier " + tier);
        plugin.sendConsole("&aChance: &2" + chance);
    }

    public void give(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            plugin.sendMessage(player, "&cYOur inventory is full!");
            return;
        }

        player.getInventory().addItem(picklock);
    }

    public void remove(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);

            if (itemStack == null)
                continue;

            if (isPickLock(itemStack)) {
                if (itemStack.getAmount() == 1)
                    player.getInventory().removeItem(itemStack);
                else
                    itemStack.setAmount(itemStack.getAmount() - 1);

                player.updateInventory();
                break;
            }
        }
    }

    public boolean isPickLock(ItemStack itemStack) {
        return picklock.isSimilar(itemStack);
    }

    public int getChance() {
        return chance;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getTier() {
        return tier;
    }

    public void flush() {
        lore.clear();
    }
}