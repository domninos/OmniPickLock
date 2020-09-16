package net.omni.picklock;

import net.omni.picklock.commands.PickLockCommand;
import net.omni.picklock.listener.PickLockListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class PickLockPlugin extends JavaPlugin {
    private List<PickLock> pickLocks;
    private PickLock pickLockTier1;
    private PickLock pickLockTier2;
    private PickLock pickLockTier3;
    private int countdown;
    private WhitelistedDoors whitelistedDoors;
    private TimerHandler timerHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        pickLockTier1 = new PickLock(this, 1);
        pickLockTier2 = new PickLock(this, 2);
        pickLockTier3 = new PickLock(this, 3);
        pickLocks = Arrays.asList(pickLockTier1, pickLockTier2, pickLockTier3);

        whitelistedDoors = new WhitelistedDoors(this);

        this.countdown = getConfig().getInt("countdown");

        this.timerHandler = new TimerHandler(this);

        registerCommands();
        registerListeners();

        sendConsole("&aSuccessfully enabled OmniPickLock v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        timerHandler.flush();
        pickLocks.forEach(PickLock::flush);
        pickLocks.clear();
        whitelistedDoors.flush();
        sendConsole("&aSuccessfully disabled OmniPickLock");
    }

    public PickLock getPickLock(ItemStack item) {
        PickLock pickLock = null;

        if (getPickLockTier1().isPickLock(item))
            pickLock = getPickLockTier1();
        else if (getPickLockTier2().isPickLock(item))
            pickLock = getPickLockTier2();
        else if (getPickLockTier3().isPickLock(item))
            pickLock = getPickLockTier3();

        return pickLock;
    }

    public void sendConsole(String message) {
        sendMessage(Bukkit.getConsoleSender(), message);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate("&7[&3PickLock&7] " + message));
    }

    private void registerCommands() {
        new PickLockCommand(this).register();
    }

    private void registerListeners() {
        new PickLockListener(this).register();
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public PickLock getPickLock(int tier) {
        return pickLocks.stream().filter(pickLock -> pickLock.getTier() == tier).findFirst().orElse(null);
    }

    public PickLock getPickLockTier1() {
        return pickLockTier1;
    }

    public PickLock getPickLockTier2() {
        return pickLockTier2;
    }

    public PickLock getPickLockTier3() {
        return pickLockTier3;
    }

    public int getCountdown() {
        return countdown;
    }

    public TimerHandler getTimerHandler() {
        return timerHandler;
    }

    public WhitelistedDoors getWhitelistedDoors() {
        return whitelistedDoors;
    }
}
