package com.jeff_media.discordspigotupdatebot.util;

import com.jeff_media.discordspigotupdatebot.DiscordSpigotUpdateBot;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

public class YamlUtils {

    public static Map<String,Object> loadFile(String fileName) {
        File file = new File(fileName);
        if(!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(DiscordSpigotUpdateBot.class.getResourceAsStream("/" + fileName)), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileReader reader = new FileReader(file)) {
            return new Yaml().load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not parse config.yml file");
        }
    }

}
