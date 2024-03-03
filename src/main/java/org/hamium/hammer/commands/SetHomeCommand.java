package org.hamium.hammer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.hamium.hammer.Hammer;
import org.hamium.hammer.utils.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetHomeCommand implements CommandExecutor {

    private final Hammer plugin;
    private final Map<UUID, Long> homeCooldowns = new HashMap<>();

    public SetHomeCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        int setCooldownMinutes = config.getInt("homes.setcooldown");
        assert setCooldownMinutes > 0;

        if (!(sender instanceof Player)) {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lOnly players can run this command.§r");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("hamium.sethome")) {
            player.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
            return true;
        }

        if (homeCooldowns.containsKey(player.getUniqueId())) {
            long lastSetTime = homeCooldowns.get(player.getUniqueId());
            long cooldown = 60 * setCooldownMinutes * 1000;
            long timeLeft = lastSetTime + cooldown - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou can only set your home once a day. Please wait " + formatTime(timeLeft) + " before setting your home again.");
                return true;
            }
        }

        setHome(player);
        player.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f Your home has been §a§lset§r§f.");

        return true;
    }

    private void setHome(Player player) {
        try (Connection connection = SQLiteDataSource.getConnection()) {
            String query = "REPLACE INTO homes (uuid, player, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setString(3, player.getWorld().getName());
            statement.setDouble(4, player.getLocation().getX());
            statement.setDouble(5, player.getLocation().getY());
            statement.setDouble(6, player.getLocation().getZ());
            statement.executeUpdate();
            homeCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes %= 60;
        seconds %= 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}
