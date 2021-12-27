package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.listener.BukkitRaidListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopRaidCommand implements CommandExecutor {
    private final LargeRaids plugin;

    public StopRaidCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BukkitRaidListener listener = plugin.getBukkitRaidListener();
        if (args.length >= 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                listener.matchingLargeRaid(player.getLocation()).ifPresent(raid -> {
                    raid.stopRaid();
                    listener.removeLargeRaid(raid);
                });
                return true;
            }
            return false;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            listener.matchingLargeRaid(player.getLocation()).ifPresent(raid -> {
                raid.stopRaid();
                listener.removeLargeRaid(raid);
            });
            return true;
        } else {
            return true;
        }
    }

}
