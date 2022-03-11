package com.jeff_media.discordspigotupdatebot.util;

import com.jeff_media.discordspigotupdatebot.DiscordSpigotUpdateBot;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

public enum YamlUtils {
    ;

    public static Map<String,Object> loadFile(final String fileName) {
        final File file = new File(fileName);
        if(!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(DiscordSpigotUpdateBot.class.getResourceAsStream("/" + fileName)), file.toPath());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        try (final FileReader reader = new FileReader(file)) {
            return new Yaml().load(reader);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Could not parse config.yml file");
        }
    }

}
