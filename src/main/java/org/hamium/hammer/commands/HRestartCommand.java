package org.hamium.hammer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.hamium.hammer.Hammer;

public class HRestartCommand implements CommandExecutor {

    private final Hammer plugin;

    public HRestartCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hrestart") && sender.isOp()) {
            plugin.restartServer(sender);
        } else {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
        }
        return true;
    }
}