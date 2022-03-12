package com.jeff_media.discordspigotupdatebot.util;

import com.jeff_media.discordspigotupdatebot.data.Plugin;

public final class MessageUtils {
    public static String applyPlaceholders(final Plugin plugin, final String text) {
        if(text == null) return null;
        return text.replace(Placeholders.NAME,plugin.name())
                .replace(Placeholders.VERSION,plugin.version())
                .replace("%spigot_link%", plugin.getSpigotLink())
                .replace("%changelog_link%", plugin.getUpdateLink())
                .replace("%download_link%", plugin.getDownloadLink())
                .replace("%date%", getTime(plugin.timestamp()))
                .replace("%thumbnail%", plugin.thumbnail() == null ? "" : plugin.thumbnail());
    }

    public static String getTime(final long timestamp) {
        return "<t:" + timestamp + ">";
    }

    private static class Placeholders {
        private static final String VERSION = "%version%";
        private static final String NAME = "%name%";
        private static final String SPIGOT_LINK = "%spigot_link%";
    }
}
