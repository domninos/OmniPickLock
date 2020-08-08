package net.omni.picklock.listener;

import me.crafter.mc.lockettepro.LocketteProAPI;
import net.omni.picklock.PickLockEvent;
import net.omni.picklock.PickLockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class PickLockListener implements Listener {
    private final PickLockPlugin plugin;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public PickLockListener(PickLockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        if (block == null)
            return;

        ItemStack item = event.getItem();

        if (item == null)
            return;

        if (!plugin.getPickLock().isPickLock(item))
            return;

        Player player = event.getPlayer();

        if (plugin.getWhitelistedDoors().isWhitelisted(block.getLocation())) {
            plugin.sendMessage(player, "&cYou cannot pick lock this door.");
            return;
        }

        if (plugin.getTimerHandler().isPicking(player)) {
            plugin.sendMessage(player, "&cYou're already picking a lock!");
            return;
        }

        if (LocketteProAPI.isLocked(block)) {
            if ((LocketteProAPI.isUpDownLockedDoor(block) && !LocketteProAPI.isUserUpDownLockedDoor(block, player))
                    || (LocketteProAPI.isSingleDoorBlock(block) && !LocketteProAPI.isUserSingleBlock(block, null, player))) {
                plugin.getTimerHandler().addPlayer(player, block);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getTimerHandler().isPicking(event.getPlayer()))
            plugin.getTimerHandler().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPickLock(PickLockEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (random.nextInt(0, 100) <= plugin.getChance()) {
            if (LocketteProAPI.isUpDownLockedDoor(block)) {
                Block doorblock = LocketteProAPI.getBottomDoorBlock(block);

                if (doorblock.getType() == Material.IRON_DOOR_BLOCK || doorblock.getType() == Material.IRON_TRAPDOOR)
                    LocketteProAPI.toggleDoor(doorblock, true);

                for (BlockFace blockface : LocketteProAPI.newsfaces) {
                    Block relative = doorblock.getRelative(blockface);

                    if (relative.getType() == doorblock.getType())
                        LocketteProAPI.toggleDoor(relative, true);
                }
            }

            LocketteProAPI.toggleDoor(block, true);

            plugin.sendMessage(player, "&aSuccessfully pick locked.");
        } else {
            plugin.getPickLock().remove(player);
            player.playEffect(EntityEffect.SHIELD_BREAK);
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
            plugin.sendMessage(player, "&cPick locking failed!");
        }
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
