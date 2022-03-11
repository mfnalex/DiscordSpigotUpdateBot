package com.jeff_media.discordspigotupdatebot.util;

import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;

public enum MessageUtils {
    ;

    public static String applyPlaceholders(final Plugin plugin, final String text) {
        if(text == null) return null;
        return text.replace("%name%",plugin.name())
                .replace("%version%",plugin.version())
                .replace("%spigot_link%", plugin.getSpigotLink())
                .replace("%changelog_link%", plugin.getUpdateLink())
                .replace("%download_link%", plugin.getDownloadLink())
                .replace("%date%", getTime(plugin.timestamp()))
                .replace("%thumbnail%", plugin.thumbnail());
    }

    public static String getTime(final long timestamp) {
        return "<t:" + timestamp + ">";
    }
}
