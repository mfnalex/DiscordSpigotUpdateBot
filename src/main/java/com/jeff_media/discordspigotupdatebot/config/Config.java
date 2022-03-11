package com.jeff_media.discordspigotupdatebot.config;

import com.jeff_media.discordspigotupdatebot.util.YamlUtils;

import java.util.Map;

public class Config {

    private final Map<String,Object> config = YamlUtils.loadFile("config.yml");

    public int getMinimumSpigetDelay() {
        return (int) config.getOrDefault("minimum-delay-between-spiget-requests",1000);
    }

    public int getInterval() {
        return (int) config.getOrDefault("interval",60);
    }

    public boolean getAnnounceNewPlugins() {
        return (boolean) config.getOrDefault("announce-new-plugins",false);
    }
}
