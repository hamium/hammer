package org.hamium.hammer.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hamium.hammer.Hammer;

public class JoinLeaveEvent implements Listener {
    private final Hammer plugin;

    public JoinLeaveEvent(Hammer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String joinMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("joinleave.joinmsg", "§7[§a§l+§r§7] §e{PLAYER}"));
        joinMessage = joinMessage.replace("{PLAYER}", player.getName());
        Bukkit.broadcastMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String leaveMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("joinleave.leavemsg", "§7[§c§l-§r§7] §e{PLAYER}"));
        leaveMessage = leaveMessage.replace("{PLAYER}", player.getName());
        Bukkit.broadcastMessage(leaveMessage);
    }
}
