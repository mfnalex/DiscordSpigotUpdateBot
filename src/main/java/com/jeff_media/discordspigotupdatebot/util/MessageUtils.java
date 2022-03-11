package com.jeff_media.discordspigotupdatebot.util;

import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.discord.DiscordManager;

public enum MessageUtils {
    ;

    public static String applyPlaceholders(final Plugin plugin, String text) {
        if(text == null) return null;
        text = text.replace("%name%",plugin.name())
                .replace("%version%",plugin.version())
                .replace("%spigot_link%", plugin.getSpigotLink())
                .replace("%changelog_link%", plugin.getUpdateLink())
                .replace("%download_link%", plugin.getDownloadLink())
                .replace("%date%", getTime(plugin.timestamp()));
        if(plugin.thumbnail() != null) {
            text = text.replace("%thumbnail%", plugin.thumbnail());
        }
        return text;
    }


    public static String getTime(final long timestamp) {
        return "<t:" + timestamp + ">";
    }
}
