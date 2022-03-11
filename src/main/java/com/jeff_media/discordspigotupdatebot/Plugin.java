package com.jeff_media.discordspigotupdatebot;

import com.jeff_media.discordspigotupdatebot.spiget.SpigetAPI;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record Plugin(String name, String version, int id, int updateId, int downloadId, String thumbnail) {

    public static final String UNDEFINED_VERSION = "UNDEFINED";
    private static final SpigetAPI spigetApi = new SpigetAPI();
    private static final String SPIGOT = "https://www.spigotmc.org/";

    public static Plugin deserialize(@NonNull String name, @NonNull Map<String,Object> map) {
        int id = (int) Objects.requireNonNull(map.get(Serialization.ID));
        String version = (String) map.getOrDefault(Serialization.VERSION,UNDEFINED_VERSION);
        int updateId = (int) map.getOrDefault(Serialization.UPDATE_ID,0);
        int downloadId = (int) map.getOrDefault(Serialization.DOWNLOAD_ID,0);
        String thumbnail = (String) map.get(Serialization.THUMBNAIL);
        return new Plugin(name, version, id, updateId, downloadId, thumbnail);
    }

    public static Plugin fromSpiget(Plugin plugin) {
        int id = plugin.id;
        String name = plugin.name;
        String thumbnail = plugin.thumbnail;
        String version = spigetApi.getVersion(id);
        int updateId = spigetApi.getUpdateId(id);
        int downloadId = spigetApi.getDownloadId(id);
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
        if(oldPlugin.version.equals(UNDEFINED_VERSION) && !this.version.equals(UNDEFINED_VERSION)) {
            return true;
        }
        if(oldPlugin.downloadId >= this.downloadId) {
            return false;
        }
        if(oldPlugin.updateId >= this.updateId) {
            return false;
        }
        return !oldPlugin.version.equals(this.version);
    }

    public Map<String,Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put(Serialization.ID,id);
        map.put(Serialization.VERSION,version);
        map.put(Serialization.DOWNLOAD_ID,downloadId);
        map.put(Serialization.UPDATE_ID,updateId);
        map.put(Serialization.THUMBNAIL,thumbnail);
        return map;
    }

    private static class Serialization {
        private static final String ID = "id";
        private static final String VERSION = "version";
        private static final String DOWNLOAD_ID = "download-id";
        private static final String UPDATE_ID = "update-id";
        private static final String THUMBNAIL = "thumbnail";
    }

}
