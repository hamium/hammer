package org.hamium.hammer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;

import org.hamium.hammer.commands.*;
import org.hamium.hammer.events.*;

public final class Hammer extends JavaPlugin {

    private Timer timer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new JoinLeaveEvent(this), this);

        getCommand("hrestart").setExecutor(new HRestartCommand(this));
        getCommand("cancelrestart").setExecutor(new CancelRestartCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("reloadhammer").setExecutor(new ReloadCommand(this));
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("suggest").setExecutor(new SuggestCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
    }

    public void restartServer(CommandSender sender) {
        if (timer != null) {
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f §c§lServer is already going to restart.§r");
            return;
        }

        Message msg = manageMessage();
        int seconds = 60;

        timer = new Timer();
        long in1Minute = Math.max(0, (seconds - 60) * 1000);
        long in3Second = Math.max(0, (seconds - 3) * 1000);
        long in2Second = Math.max(0, (seconds - 2) * 1000);
        long in1Second = Math.max(0, (seconds - 1) * 1000);

        timer.schedule(new WarnTask(msg.getMin_1(), Boolean.FALSE), in1Minute);
        timer.schedule(new WarnTask(msg.getSec_3(), Boolean.FALSE), in3Second);
        timer.schedule(new WarnTask(msg.getSec_2(), Boolean.FALSE), in2Second);
        timer.schedule(new WarnTask(msg.getSec_1(), Boolean.FALSE), in1Second);
        timer.schedule(new WarnTask(msg.getNow(), Boolean.TRUE), seconds * 1000L);
    }

    public void cancelRestart(CommandSender sender) {
        getLogger().info("Canceling restart...");
        if (timer != null) {
            timer.cancel();
            timer = null;
            getLogger().info("Canceled restart!");
            Bukkit.broadcastMessage("§d§l፨ §nhamiumMC§r §5§l|§r§f Restart has been §c§lcancelled§r§f.");
        } else {
            getLogger().info("No restart in progress.");
            sender.sendMessage("§d§l፨ §nhamiumMC§r §5§l| §r§c§lThere isn't a restart in progress§r");
        }
    }

    private Message manageMessage() {
        String min1 = getConfigString("hrestart.1min", "§d§l፨ §nhamiumMC§r §5§l|§r§f Restarting in §c§l1 minute§r§f!");
        String sec3 = getConfigString("hrestart.3sec", "§d§l፨ §nhamiumMC§r §5§l|§r§f Restarting in §c§l3 seconds§r§f!");
        String sec2 = getConfigString("hrestart.2sec", "§d§l፨ §nhamiumMC§r §5§l|§r§f Restarting in §c§l2 seconds§r§f!");
        String sec1 = getConfigString("hrestart.1sec", "§d§l፨ §nhamiumMC§r §5§l|§r§f Restarting in §c§l1 second§r§f!");
        String now = getConfigString("hrestart.now", "§d§l፨ §nhamiumMC§r §5§l|§r§f Restarting...");

        return new Message(min1, sec3, sec2, sec1, now);
    }

    public String getConfigString(String path, String defaultValue) {
        String value = getConfig().getString(path);
        return (value != null) ? ChatColor.translateAlternateColorCodes('&', value) : defaultValue;
    }

    static class Message {
        private final String min_1;
        private final String sec_3;
        private final String sec_2;
        private final String sec_1;
        private final String now;

        public Message(String min1, String sec3, String sec2, String sec1, String now) {
            this.min_1 = min1;
            this.sec_3 = sec3;
            this.sec_2 = sec2;
            this.sec_1 = sec1;
            this.now = now;
        }

        public String getMin_1() {
            return this.min_1;
        }

        public String getSec_3() {
            return this.sec_3;
        }

        public String getSec_2() {
            return this.sec_2;
        }

        public String getSec_1() {
            return this.sec_1;
        }

        public String getNow() {
            return this.now;
        }
    }

    static class WarnTask extends TimerTask {
        private final String message;
        private final Boolean reboot;

        WarnTask(String message, Boolean reboot) {
            this.message = message;
            this.reboot = reboot;
        }

        public void run() {
            Bukkit.broadcastMessage(message);
            if (this.reboot) {
                Bukkit.getServer().savePlayers();
                Bukkit.spigot().restart();
            }
        }
    }
}
