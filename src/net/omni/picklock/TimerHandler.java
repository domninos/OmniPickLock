package net.omni.picklock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimerHandler {
    private final ConcurrentHashMap<Player, Integer> playerPicking = new ConcurrentHashMap<>();
    private final Map<Player, Block> playerBlock = new HashMap<>();
    private final PickLockPlugin plugin;

    public TimerHandler(PickLockPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<Player, Integer> entry : playerPicking.entrySet()) {
                Player player = entry.getKey();

                if (player == null)
                    continue;

                if (entry.getValue() <= 0) {
                    Block block = playerBlock.get(player);

                    if (block != null)
                        Bukkit.getPluginManager().callEvent(new PickLockEvent(player, block));

                    removePlayer(player);
                    continue;
                }

                entry.setValue(entry.getValue() - 1);

                player.sendTitle(ChatColor.RED + "Pick locking...",
                        ChatColor.GREEN + String.valueOf(entry.getValue()), 10, 20, 10);
                plugin.sendMessage(player, "&aPick locking...");
            }
        }, 20L, 20L);
    }

    public void addPlayer(Player player, Block block) {
        playerPicking.put(player, plugin.getCountdown());
        playerBlock.put(player, block);
    }

    public boolean isPicking(Player player) {
        return playerPicking.containsKey(player);
    }

    public void removePlayer(Player player) {
        playerPicking.remove(player);
        playerBlock.remove(player);
    }

    public void flush() {
        playerPicking.clear();
        playerBlock.clear();
    }
}
