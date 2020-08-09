package net.omni.picklock.commands;

import net.omni.picklock.PickLock;
import net.omni.picklock.PickLockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PickLockCommand implements CommandExecutor {
    private final PickLockPlugin plugin;

    public PickLockCommand(PickLockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("picklock.use"))
            return noPerms(sender);

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission("picklock.give"))
                    return noPerms(sender);

                plugin.sendMessage(sender, "&cUsage: /picklock give <player> <tier>");
            } else if (args[0].equalsIgnoreCase("door"))
                plugin.sendMessage(sender, "&cUsage: /picklock door <add|remove>");
            else
                sendHelp(sender);

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission("picklock.give"))
                    return noPerms(sender);

                plugin.sendMessage(sender, "&cUsage: /picklock give <player> <tier>");
            } else if (args[0].equalsIgnoreCase("door")) {
                if (!(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    sendHelp(sender);
                    return true;
                }

                if (!(sender instanceof Player)) {
                    plugin.sendMessage(sender, "&cOnly players can use this command.");
                    return true;
                }

                Player player = (Player) sender;

                if (!(player.hasPermission("picklock.add") || player.hasPermission("picklock.remove")))
                    return noPerms(player);

                Block targetBlock = player.getTargetBlock(null, 5);

                if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                    plugin.sendMessage(player, "&cBlock not found.");
                    return true;
                }

                if (args[1].equalsIgnoreCase("add")) {
                    if (plugin.getWhitelistedDoors().isWhitelisted(targetBlock.getLocation())) {
                        plugin.sendMessage(player, "&cThat location is already added.");
                        return true;
                    }

                    plugin.getWhitelistedDoors().addDoor(targetBlock.getLocation());
                    plugin.sendMessage(player, "&aSuccessfully whitelisted door.");
                } else {
                    if (!(plugin.getWhitelistedDoors().isWhitelisted(targetBlock.getLocation()))) {
                        plugin.sendMessage(player, "&cThat location is not whitelisted.");
                        return true;
                    }

                    plugin.getWhitelistedDoors().removeDoor(targetBlock.getLocation());
                    plugin.sendMessage(player, "&aSuccessfully unwhitelisted block.");
                }

                return true;
            } else
                sendHelp(sender);

            return true;
        } else if (args.length == 3) {
            if (!sender.hasPermission("picklock.give"))
                return noPerms(sender);

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                plugin.sendMessage(sender, "&cPlayer not found.");
                return true;
            }

            int tier;

            try {
                tier = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.sendMessage(sender, "&cTier is not a number!");
                return true;
            }

            PickLock pickLock = plugin.getPickLock(tier);

            if (pickLock == null) {
                plugin.sendConsole("&cPickLock not found.");
                return true;
            }

            pickLock.give(target);
            plugin.sendMessage(sender, "&aSuccessfully given " + target.getName() + " pick lock tier " + tier);
            return true;
        } else
            sendHelp(sender);

        return true;
    }

    private void sendHelp(CommandSender sender) {
        plugin.sendMessage(sender, "&cUsage: /picklock give <player> <tier>");
        plugin.sendMessage(sender, "&cUsage: /picklock door <add|remove>");
    }

    private boolean noPerms(CommandSender sender) {
        plugin.sendMessage(sender, "&cYou do not have permissions to use this command.");
        return true;
    }

    public void register() {
        plugin.getCommand("picklock").setExecutor(this);
    }
}
