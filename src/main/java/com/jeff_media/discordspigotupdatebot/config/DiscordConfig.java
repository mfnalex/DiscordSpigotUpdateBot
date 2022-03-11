package com.jeff_media.discordspigotupdatebot.config;

import com.jeff_media.discordspigotupdatebot.util.YamlUtils;

import java.util.Map;

public class DiscordConfig {

    private final Map<String,Object> config = YamlUtils.loadFile("discord.yml");

    public String getBotToken() {
        String botToken = getOrThrow("bot-token");
        if(botToken.equals("your-bot-token")) {
            throw new IllegalArgumentException("Setup a valid bot-token in discord.yml.");
        }
        return botToken;
    }

    public String getChannelId() {
        return getOrThrow("channel-id");
    }

    private String getOrThrow(String path) {
        if(config.containsKey(path)) return (String) config.get(path);
        throw new IllegalArgumentException("DiscordConfig option " + path + " not set");
    }

}
