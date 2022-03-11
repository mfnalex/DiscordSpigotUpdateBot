package com.jeff_media.discordspigotupdatebot;

import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;
import org.slf4j.Logger;

import java.util.Map;
import java.util.TimerTask;

public class UpdateCheckerTask extends TimerTask {

    private final DiscordSpigotUpdateBot main = DiscordSpigotUpdateBot.getInstance();
    private final Logger logger = DiscordSpigotUpdateBot.getLogger();
    private final Map<String, Plugin> plugins = main.getPlugins();
    private final DiscordManager discordManager = main.getDiscordManager();

    @Override
    public void run() {
        logger.debug("Checking for updates...");
        for(final String name : plugins.keySet()) {
            final Plugin oldPlugin = plugins.get(name);
            final Plugin newPlugin = Plugin.fromSpiget(oldPlugin);
            logger.debug("Got answer for " + name + ": " + newPlugin);
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
