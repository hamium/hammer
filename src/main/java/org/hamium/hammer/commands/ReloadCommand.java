package org.hamium.hammer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.hamium.hammer.Hammer;

public class ReloadCommand implements CommandExecutor {
    private final Hammer plugin;

    public ReloadCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("reloadhammer") && sender.hasPermission("hamium.reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §a§lConfiguration reloaded successfully.");
            return true;
        } else {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
        }
        return true;
    }
}
