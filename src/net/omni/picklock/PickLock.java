package net.omni.picklock;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class PickLock {
    private final PickLockPlugin plugin;
    private Material material;
    private String name;
    private List<String> lore;
    private ItemStack picklock;

    public PickLock(PickLockPlugin plugin) {
        this.plugin = plugin;

        String path = "picklock.";

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
        meta.setDisplayName(name);
        meta.setLore(lore.stream().map(l -> plugin.translate(l)).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        this.picklock = itemStack;
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

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void flush() {
        lore.clear();
    }
}