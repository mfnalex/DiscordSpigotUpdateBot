package com.jeff_media.discordspigotupdatebot;

import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;
import com.jeff_media.discordspigotupdatebot.spiget.PluginRemovedException;
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
            if(oldPlugin.id() == -1) continue;
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
                } catch (final Exception exception) {
                    logger.warn("Could not send embed to Discord", exception);
                }
                main.savePluginsToFile();
            } catch (final Exception exception) {
                if(exception instanceof PluginRemovedException && oldPlugin.isValid()) {
                    logger.warn("Plugin " + oldPlugin.name() + " has been removed from SpigotMC!");
                    if(main.getConfig().getAnnounceDeletedPlugins()) {
                        discordManager.sendWarningEmbed(oldPlugin);
                    }
                    plugins.put(name, new Plugin(name,Plugin.UNDEFINED_VERSION,-1,-1,-1,oldPlugin.thumbnail(),-1));
                    main.savePluginsToFile();
                    continue;
                }
                logger.warn("Could not fetch updates for plugin " + name + ". Please check if the given ID (" + oldPlugin.id() + ") is correct. If this plugin has been uploaded to SpigotMC recently, do not worry, it'll work soon.");
            }
        }
    }
}
