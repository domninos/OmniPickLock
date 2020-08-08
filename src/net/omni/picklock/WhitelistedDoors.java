package net.omni.picklock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class WhitelistedDoors {
    private final List<Location> doors = new ArrayList<>();
    private final PickLockPlugin plugin;

    public WhitelistedDoors(PickLockPlugin plugin) {
        this.plugin = plugin;

        loadDoors();
    }

    public void loadDoors() {
        doors.clear();

        List<String> section = plugin.getConfig().getStringList("whitelisted");

        if (section == null) {
            plugin.sendConsole("&cWhitelisted doors not found in config.yml");
            return;
        }

        for (String key : section) {
            if (key == null)
                continue;

            String[] split = key.split(",");

            if (split.length <= 2) {
                plugin.sendConsole("&cCould not load door because coordinates are too low.");
                continue;
            }

            String worldString = split[0];
            String xString = split[1];
            String yString = split[2];
            String zString = split[3];

            int x;
            int y;
            int z;

            try {
                x = Integer.parseInt(xString);
                y = Integer.parseInt(yString);
                z = Integer.parseInt(zString);
            } catch (NumberFormatException e) {
                plugin.sendConsole("&cSomething went wrong loading location. A coordinate's not a number.");
                continue;
            }

            World world = Bukkit.getWorld(worldString);

            if (world == null) {
                plugin.sendConsole("&cWorld is not found.");
                continue;
            }

            doors.add(new Location(world, x, y, z));
            plugin.sendConsole("&aDEBUG door added.");
        }

        plugin.sendConsole("&aSuccessfully loaded doors.");
    }

    public void addDoor(Location location) {
        if (location == null)
            return;

        String world = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        doors.add(location);

        List<String> copy = plugin.getConfig().getStringList("whitelisted");

        copy.add(world + "," + x + "," + y + "," + z);

        plugin.getConfig().set("whitelisted", copy);
        plugin.saveConfig();
        plugin.sendConsole("&aSuccessfully added door.");
    }

    public void removeDoor(Location location) {
        if (location == null)
            return;

        if (!isWhitelisted(location))
            return;

        doors.remove(location);

        List<String> copy = plugin.getConfig().getStringList("whitelisted");

        copy.remove(location.getWorld().getName() + "," + location.getBlockX()
                + "," + location.getBlockY() + "," + location.getBlockZ());

        plugin.getConfig().set("whitelisted", copy);
        plugin.saveConfig();
        plugin.sendConsole("&aSuccessfully removed door.");
    }

    public boolean isWhitelisted(Location location) {
        return location != null && !doors.isEmpty() && doors.stream().anyMatch(loc -> loc.equals(location));
    }

    public void flush() {
        doors.clear();
    }
}