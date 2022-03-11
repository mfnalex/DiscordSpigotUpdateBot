package com.jeff_media.discordspigotupdatebot;

import com.jeff_media.discordspigotupdatebot.config.Config;
import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;
import com.jeff_media.discordspigotupdatebot.util.YamlUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DiscordSpigotUpdateBot {

    @Getter private static DiscordSpigotUpdateBot instance;
    @Getter private static final Logger logger = LoggerFactory.getLogger(DiscordSpigotUpdateBot.class);
    @Getter private final Config config = new Config();
    @Getter private final DiscordManager discordManager = new DiscordManager();
    @Getter private Map<String,Plugin> plugins;

    public static void main(String[] args) {
        new DiscordSpigotUpdateBot();
    }

    public DiscordSpigotUpdateBot() {
        if(instance != null) {
            throw new IllegalStateException("Already initialized");
        }
        instance = this;
        loadPluginsFromFile();
        new Timer().schedule(new UpdateCheckerTask(), 0, config.getInterval()* 1000L);
    }

    private void loadPluginsFromFile() {
        logger.info("Loading plugins from file...");
        plugins = new HashMap<>();
        final Map<String,Object> map = YamlUtils.loadFile("plugins.yml");
        for(Map.Entry<String,Object> entry : map.entrySet()) {
            final String name = entry.getKey();
            final Map<String,Object> pluginData = (Map<String, Object>) entry.getValue();
            final Plugin plugin = Plugin.deserialize(name, pluginData);
            logger.info("Loaded plugin: " + plugin);
            plugins.put(name,plugin);
        }
        logger.info("Loaded " + plugins.size() + " plugins.");
    }

    public void savePluginsToFile() {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        final Map<String,Object> map = new HashMap<>();
        for(Map.Entry<String,Plugin> entry : plugins.entrySet()) {
            map.put(entry.getKey(),entry.getValue().serialize());
        }
        final File file = new File("plugins.yml");
        try(final FileWriter writer = new FileWriter(file)) {
            new Yaml(dumperOptions).dump(map,writer);
            logger.info("Successfully saved plugins.yml");
        } catch (IOException e) {
            logger.error("Could not save plugins.yml",e);
        }
    }
}
