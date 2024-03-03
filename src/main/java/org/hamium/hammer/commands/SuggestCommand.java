package org.hamium.hammer.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.hamium.hammer.Hammer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SuggestCommand implements CommandExecutor {
    private final Hammer plugin;

    public SuggestCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("suggest") && sender.hasPermission("hamium.suggest")) {
            if (args.length < 1) {
                sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lUsage: /suggest <suggestion>");
                return true;
            }

            StringBuilder suggestionBuilder = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                suggestionBuilder.append(args[i]).append(" ");
            }

            String suggestion = suggestionBuilder.toString().trim();
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §a§lSuggestion submitted!");
            sendDiscordSuggestion(sender.getName(), suggestion);
        } else {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
        }
        return true;
    }

    private void sendDiscordSuggestion(String suggester, String suggestion) {
        try {
            FileConfiguration config = plugin.getConfig();
            String webhookUrl = config.getString("webhooks.suggestions");
            assert webhookUrl != null;
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = String.format("{\"embeds\":[{\"title\":\"Suggestion by %s\",\"description\":\"**Suggestion:** %s\"}]}", suggester, suggestion);
            connection.getOutputStream().write(payload.getBytes());
            connection.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace(); // womp womp
        }
    }
}
