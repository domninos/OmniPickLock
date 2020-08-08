package net.omni.picklock;

import net.omni.picklock.commands.PickLockCommand;
import net.omni.picklock.listener.PickLockListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class PickLockPlugin extends JavaPlugin {
    private PickLock pickLock;
    private int chance;
    private int countdown;
    private WhitelistedDoors whitelistedDoors;
    private TimerHandler timerHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        pickLock = new PickLock(this);
        whitelistedDoors = new WhitelistedDoors(this);

        sendConsole("&aLoading picklock..");
        sendConsole("&aType: &2" + pickLock.getMaterial().name());
        sendConsole("&aName: " + pickLock.getName());
        sendConsole("&aLore:");

        pickLock.getLore().forEach(this::sendConsole);

        this.chance = getConfig().getInt("picklockChance");
        this.countdown = getConfig().getInt("countdown");
        sendConsole("&aChance: &2" + chance);

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
        pickLock.flush();
        whitelistedDoors.flush();
        sendConsole("&aSuccessfully disabled OmniPickLock");
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

    public PickLock getPickLock() {
        return pickLock;
    }

    public int getChance() {
        return chance;
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
