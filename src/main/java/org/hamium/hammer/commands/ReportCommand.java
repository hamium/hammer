package org.hamium.hammer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.hamium.hammer.Hammer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportCommand implements CommandExecutor {
    private final Hammer plugin;

    public ReportCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("report") && sender.hasPermission("hamium.report")) {
            if (args.length < 2) {
                sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lUsage: /report <player> <reason>");
                return true;
            }

            String reportedPlayer = args[0];
            String reason = "";
            for (int i = 1; i < args.length; i++) {
                reason += args[i] + " ";
            }

            reason = reason.trim();
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §a§lPlayer reported!");
            sendDiscordReport(reportedPlayer, reason, sender.getName());
        } else {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
        }
        return true;
    }

    private void sendDiscordReport(String reportedPlayer, String reason, String reporter) {
        try {
            FileConfiguration config = plugin.getConfig();
            String webhookUrl = config.getString("webhooks.reports");
            assert webhookUrl != null;
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = String.format("{\"embeds\":[{\"title\":\"%s reported by %s\",\"description\":\"**Reason:** %s\"}]}", reportedPlayer, reporter, reason);
            connection.getOutputStream().write(payload.getBytes());
            connection.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace(); // womp womp
        }
    }
}
