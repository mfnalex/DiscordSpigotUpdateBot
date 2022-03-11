package com.jeff_media.discordspigotupdatebot.spiget;

import com.google.gson.JsonParser;
import com.jeff_media.discordspigotupdatebot.DiscordSpigotUpdateBot;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class SpigetAPI {

    private static final String API_LAST_UPDATE = "https://api.spiget.org/v2/resources/%d/updates/latest";
    private static final String API_LAST_VERSION = "https://api.spiget.org/v2/resources/%d/versions/latest";
    private static final String API_RESOURCE_DETAILS = "https://api.spiget.org/v2/resources/%d";

    private final int minimumRequestDelay = DiscordSpigotUpdateBot.getInstance().getConfig().getMinimumSpigetDelay();
    private long lastRequest = 0;

    public String getVersion(long id) {
        String response = getHttp(String.format(API_LAST_VERSION,id));
        return JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
    }

    public int getUpdateId(long id) {
        String response = getHttp(String.format(API_LAST_UPDATE,id));
        return JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
    }

    public int getDownloadId(long id) {
        String response = getHttp(String.format(API_RESOURCE_DETAILS,id));
        String downloadPath = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("file").getAsJsonPrimitive("url").getAsString();
        String[] split = downloadPath.split("=");
        return Integer.parseInt(split[split.length-1]);
    }

    private String getHttp(String url) {
        waitIfNeeded();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent","DiscordSpigotUpdateBot/1.0")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new IllegalStateException("Could not get API response:",e);
        }
    }

    private void waitIfNeeded() {
        if(System.currentTimeMillis() < lastRequest + minimumRequestDelay) {
            try {
                Thread.sleep(lastRequest + minimumRequestDelay - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastRequest = System.currentTimeMillis();
    }
}
