package org.hamium.hammer.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.hamium.hammer.Hammer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BroadcastCommand implements CommandExecutor {

    private final Hammer plugin;

    public BroadcastCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("broadcast") && sender.hasPermission("hamium.broadcast") || sender.isOp()) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage!\nUsage : /broadcast <message>");
                return true;
            }
            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }

            String formattedMessage = ChatColor.translateAlternateColorCodes('&', "&e&l---------------------------\n&r&f" + message + "\n&e&l---------------------------&r");
            Bukkit.broadcastMessage(formattedMessage);
            sendDiscordBroadcast(String.valueOf(message));
            return true;
        } else {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
        }
        return true;
    }

    private void sendDiscordBroadcast(String broadcast) {
        try {
            FileConfiguration config = plugin.getConfig();
            String webhookUrl = config.getString("webhooks.broadcasts");
            assert webhookUrl != null;
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = String.format("{\"embeds\":[{\"title\":\"\\uD83D\\uDCE2 Broadcast \\uD83D\\uDCE2\",\"description\":\"%s\", \"color\": 16711680}]}", broadcast);
            connection.getOutputStream().write(payload.getBytes());

            connection.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace(); // womp womp
        }
    }
}
