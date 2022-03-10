package com.jeff_media.discordspigotupdatebot;

import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;
import com.jeff_media.discordspigotupdatebot.logging.SimpleLogFormatter;
import com.jeff_media.discordspigotupdatebot.util.YamlUtils;
import lombok.Getter;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class DiscordSpigotUpdateBot {

    @Getter private static DiscordSpigotUpdateBot instance;
    private final DiscordManager discordManager;
    private Map<String,Plugin> plugins;
    @Getter private static final Logger logger = Logger.getLogger(DiscordSpigotUpdateBot.class.getName());

    public static void main(String[] args) {
        logger.setUseParentHandlers(false);
        SimpleLogFormatter formatter = new SimpleLogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        new DiscordSpigotUpdateBot();
    }

    public DiscordSpigotUpdateBot() {
        instance = this;
        discordManager = new DiscordManager();
        loadPluginsFromFile();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for(String name : plugins.keySet()) {
                    Plugin oldPlugin = plugins.get(name);
                    Plugin newPlugin = Plugin.fromSpiget(oldPlugin);
                    if(!newPlugin.isNewerThan(oldPlugin)) continue;
                    logger.info("Found new version for " + newPlugin.name()+":");
                    logger.info("Old version: " + oldPlugin);
                    logger.info("New version: " + newPlugin);
                    discordManager.sendUpdateEmbed(newPlugin);
                    plugins.put(name, newPlugin);
                }
            }
        };
        new Timer().schedule(task, 60*1000, 60*1000);
    }

    private void loadPluginsFromFile() {
        plugins = new HashMap<>();
        Map<String,Object> map = YamlUtils.loadFile("plugins.yml");
        for(Map.Entry<String,Object> entry : map.entrySet()) {
            String name = entry.getKey();
            Map<String,Object> pluginData = (Map<String, Object>) entry.getValue();
            Plugin plugin = Plugin.fromFile(name, pluginData);
            logger.info("Loaded plugin from file: " + plugin);
            plugins.put(name,plugin);
        }
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not sleep");
        }
    }

}
