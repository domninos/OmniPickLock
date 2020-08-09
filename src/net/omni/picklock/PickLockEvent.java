package net.omni.picklock;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PickLockEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Block block;
    private final PickLock pickLock;

    public PickLockEvent(Player player, Block block, PickLock pickLock) {
        this.player = player;
        this.block = block;
        this.pickLock = pickLock;

    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public PickLock getPickLock() {
        return pickLock;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
