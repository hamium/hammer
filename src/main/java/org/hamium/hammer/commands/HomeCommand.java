package org.hamium.hammer.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.hamium.hammer.Hammer;
import org.hamium.hammer.utils.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final Hammer plugin;
    private final Map<UUID, Long> homeCooldowns = new HashMap<>();

    public HomeCommand(Hammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        int tpCooldownMinutes = config.getInt("homes.tpcooldown");
        assert tpCooldownMinutes > 0;

        if (!(sender instanceof Player)) {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lOnly players can run this command.§r");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("hamium.home")) {
            player.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou are missing permissions to execute this command.§r");
            return true;
        }

        if (homeCooldowns.containsKey(player.getUniqueId())) {
            long lastUseTime = homeCooldowns.get(player.getUniqueId());
            long cooldown = 60 * tpCooldownMinutes * 1000;
            long timeLeft = lastUseTime + cooldown - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou can only use your home once an hour. Please wait " + formatTime(timeLeft) + " before using your home again.");
                return true;
            }
        }

        teleportToHome(player);
        player.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f Teleported you to your §a§lhome§r§f!.");

        return true;
    }

    private void teleportToHome(Player player) {
        try (Connection connection = SQLiteDataSource.getConnection()) {
            String query = "SELECT * FROM homes WHERE uuid=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String worldName = resultSet.getString("world");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");

                Location homeLocation = new Location(plugin.getServer().getWorld(worldName), x, y, z);
                player.teleport(homeLocation);
                homeCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            } else {
                player.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lYou haven't set a home! Run /sethome to set your home.§r");
            }
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
