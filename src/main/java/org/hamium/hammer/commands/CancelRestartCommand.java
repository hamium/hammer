package org.hamium.hammer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.hamium.hammer.Hammer;

public class CancelRestartCommand implements CommandExecutor {

    private final Hammer plugin;

    public CancelRestartCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("cancelrestart") && sender.isOp()) {
            plugin.cancelRestart(sender);
        } else {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
        }
        return true;
    }
}