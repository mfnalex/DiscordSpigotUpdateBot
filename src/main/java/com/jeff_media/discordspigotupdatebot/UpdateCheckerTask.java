package com.jeff_media.discordspigotupdatebot;

import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;
import org.slf4j.Logger;

import java.util.Map;
import java.util.TimerTask;

public class UpdateCheckerTask extends TimerTask {

    private final DiscordSpigotUpdateBot main = DiscordSpigotUpdateBot.getInstance();
    private final Logger logger = DiscordSpigotUpdateBot.getLogger();
    private final Map<String,Plugin> plugins = main.getPlugins();
    private final DiscordManager discordManager = main.getDiscordManager();

    @Override
    public void run() {
        for(String name : plugins.keySet()) {
            Plugin oldPlugin = plugins.get(name);
            Plugin newPlugin = Plugin.fromSpiget(oldPlugin);
            if(!newPlugin.isNewerThan(oldPlugin)) continue;
            logger.info("Found new version for " + newPlugin.name()+":");
            logger.info("Old version: " + oldPlugin);
            logger.info("New version: " + newPlugin);
            plugins.put(name, newPlugin);
            if(!oldPlugin.version().equals(Plugin.UNDEFINED_VERSION) || main.getConfig().getAnnounceNewPlugins()) {
                discordManager.sendUpdateEmbed(newPlugin);
            }
            main.savePluginsToFile();
        }
    }
}
