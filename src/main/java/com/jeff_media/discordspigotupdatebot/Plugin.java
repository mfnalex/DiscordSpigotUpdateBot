package com.jeff_media.discordspigotupdatebot;

import com.jeff_media.discordspigotupdatebot.spiget.SpigetAPI;

import java.util.Map;
import java.util.Objects;

public record Plugin(String name, String version, int id, int updateId, int downloadId, String thumbnail) {

    private static final String SPIGOT = "https://www.spigotmc.org/";

    public static Plugin fromFile(String name, Map<String,Object> map) {
        Objects.requireNonNull(name);
        String version = (String) Objects.requireNonNull(map.get("version"));
        int id = (int) Objects.requireNonNull(map.get("id"));
        int updateId = (int) Objects.requireNonNull(map.get("update-id"));
        int downloadId = (int) Objects.requireNonNull(map.get("download-id"));
        String thumbnail = (String) map.get("thumbnail");
        return new Plugin(name, version, id, updateId, downloadId, thumbnail);
    }

    public static Plugin fromSpiget(Plugin plugin) {
        int id = plugin.id;
        String name = plugin.name;
        String thumbnail = plugin.thumbnail;
        String version = SpigetAPI.getVersion(id);
        int updateId = SpigetAPI.getUpdateId(id);
        int downloadId = SpigetAPI.getDownloadId(id);
        return new Plugin(name, version, id, updateId, downloadId, thumbnail);
    }

    public String getSpigotLink() {
        return SPIGOT + "resources/" + id + "/";
    }

    public String getDownloadLink() {
        return getSpigotLink() + "download?version=" + downloadId;
    }

    public String getUpdateLink() {
        return getSpigotLink() + "update?update=" + updateId;
    }

    public boolean isNewerThan(Plugin oldPlugin) {
        if(this.id != oldPlugin.id) throw new IllegalArgumentException("Cannot compare two different plugins");
        if(oldPlugin.downloadId >= this.downloadId) {
            return false;
        }
        if(oldPlugin.updateId >= this.updateId) {
            return false;
        }
        if(oldPlugin.version.equals(this.version)) {
            return false;
        }
        return true;
    }

}
