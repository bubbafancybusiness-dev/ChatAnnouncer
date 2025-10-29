package com.example.chatannouncer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class ChatAnnouncer extends JavaPlugin {

    private List<String> messages;
    private int messageIndex;
    private BukkitTask announcementTask;

    @Override
    public void onEnable() {
        // Save the default config.yml if it doesn't exist
        saveDefaultConfig();

        // Load configuration
        loadConfiguration();

        // Schedule announcements
        scheduleAnnouncements();

        getLogger().info("ChatAnnouncer has been enabled!");
    }

    @Override
    public void onDisable() {
        // Cancel the announcement task to prevent memory leaks
        if (announcementTask != null) {
            announcementTask.cancel();
        }
        getLogger().info("ChatAnnouncer has been disabled!");
    }

    private void loadConfiguration() {
        // Reload the config from disk
        reloadConfig();
        messages = getConfig().getStringList("messages");
        messageIndex = 0;
    }

    private void scheduleAnnouncements() {
        if (messages == null || messages.isEmpty()) {
            getLogger().warning("No messages found in config.yml. Disabling announcements.");
            return;
        }

        long interval = getConfig().getLong("interval", 300) * 20L; // Ticks (20 ticks = 1 second)

        announcementTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!messages.isEmpty()) {
                // Get the current message
                String message = messages.get(messageIndex);

                // Support for multiline and color codes
                String[] lines = message.split("\\\\n");
                for (String line : lines) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line));
                }

                // Move to the next message
                messageIndex = (messageIndex + 1) % messages.size();
            }
        }, 0L, interval);
    }
}
