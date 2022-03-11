package com.jeff_media.discordspigotupdatebot;

import ch.qos.logback.classic.Level;
import com.jeff_media.discordspigotupdatebot.config.Config;
import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;
import com.jeff_media.discordspigotupdatebot.util.YamlUtils;
import lombok.Getter;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DiscordSpigotUpdateBot {

    @Getter private static final Logger logger = (Logger) LoggerFactory.getLogger(DiscordSpigotUpdateBot.class);
    @Getter private static DiscordSpigotUpdateBot instance;
    @Getter private final Config config = new Config();
    @Getter private final DiscordManager discordManager;
    @Getter private Map<String, Plugin> plugins;
    private static final Properties PROPERTIES = new Properties();
    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();

    static {

        try(final InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("project.properties")) {
            PROPERTIES.load(inputStream);
        } catch (final IOException e) {
            throw new IllegalStateException("Could not load project.properties");
        }

        DUMPER_OPTIONS.setIndent(2);
        DUMPER_OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        DUMPER_OPTIONS.setPrettyFlow(true);
    }

    public DiscordSpigotUpdateBot(final String[] args) {

        if(instance != null) {
            throw new IllegalStateException("Already initialized");
        }
        instance = this;

        for(final String arg : args) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (arg) {
                case "--debug":
                    logger.setLevel(Level.ALL);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        logger.info("Loading " + PROPERTIES.getProperty("name") + " v" + PROPERTIES.getProperty("version") + " by JEFF Media GbR / mfnalex");
        logger.info("GitHub: https://github.com/JEFF-Media-GbR/DiscordSpigotUpdateBot");
        logger.info("");

        loadPluginsFromFile();

        discordManager = new DiscordManager();

        logger.info("Checking for updates every " + config.getInterval() + " seconds...");
        new Timer().schedule(new UpdateCheckerTask(), 0, config.getInterval()* 1000L);
    }

    private void loadPluginsFromFile() {
        logger.info("Loading plugins from file...");
        plugins = new HashMap<>();
        final Map<String,Object> map = YamlUtils.loadFile("plugins.yml");
        for(final Map.Entry<String,Object> entry : map.entrySet()) {
            final String name = entry.getKey();
            @SuppressWarnings("unchecked") final Map<String,Object> pluginData = (Map<String, Object>) entry.getValue();
            final Plugin plugin = Plugin.deserialize(name, pluginData);
            logger.debug("Loaded plugin: " + plugin);
            plugins.put(name,plugin);
        }
        logger.info("Loaded " + plugins.size() + " plugins.");
    }

    public void savePluginsToFile() {
        final Map<String,Object> map = new HashMap<>();
        for(final Map.Entry<String,Plugin> entry : plugins.entrySet()) {
            if(entry.getValue().id()!=-1) { // Do not save deleted plugins
                map.put(entry.getKey(), entry.getValue().serialize());
            }
        }
        final File file = new File("plugins.yml");
        try(final FileWriter writer = new FileWriter(file)) {
            new Yaml(DUMPER_OPTIONS).dump(map,writer);
            logger.info("Successfully saved plugins.yml");
        } catch (final IOException e) {
            logger.error("Could not save plugins.yml",e);
        }
    }
}
