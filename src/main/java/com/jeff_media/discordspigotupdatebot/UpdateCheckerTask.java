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
            try {
                final Plugin newPlugin = Plugin.fromSpiget(oldPlugin);
                logger.debug("Got answer for " + name + ": " + newPlugin);
                if (!newPlugin.isNewerThan(oldPlugin)) continue;
                logger.info("Found new version for " + newPlugin.name() + ":");
                logger.info("Old version: " + oldPlugin);
                logger.info("New version: " + newPlugin);
                plugins.put(name, newPlugin);
                try {
                    if (!oldPlugin.version().equals(Plugin.UNDEFINED_VERSION) || main.getConfig().getAnnounceNewPlugins()) {
                        discordManager.sendUpdateEmbed(newPlugin);
                    }
                } catch (Exception exception) {
                    logger.warn("Could not send embed to Discord", exception);
                }
                main.savePluginsToFile();
            } catch (Exception exception) {
                logger.warn("Could not fetch updates for plugin " + name + ". Please check if the given ID (" + oldPlugin.id() + ") is correct. If this plugin has been uploaded to SpigotMC recently, do not worry, it'll work soon.");
            }
        }
    }
}
