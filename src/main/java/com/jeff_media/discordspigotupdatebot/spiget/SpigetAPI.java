package com.jeff_media.discordspigotupdatebot.spiget;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jeff_media.discordspigotupdatebot.DiscordSpigotUpdateBot;
import com.jeff_media.discordspigotupdatebot.data.PluginUpdate;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Objects;

public class SpigetAPI {

    private static final String API_LAST_UPDATE = "https://api.spiget.org/v2/resources/%d/updates/latest";
    private static final String API_LAST_VERSION = "https://api.spiget.org/v2/resources/%d/versions/latest";
    private static final String API_RESOURCE_DETAILS = "https://api.spiget.org/v2/resources/%d";

    private final int minimumRequestDelay = DiscordSpigotUpdateBot.getInstance().getConfig().getMinimumSpigetDelay();
    private long lastRequest = 0;

    public String getVersion(final long id) {
        final String response = getHttp(String.format(API_LAST_VERSION,id));
        return JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
    }

    public PluginUpdate getUpdate(final long id) {
        final String response = getHttp(String.format(API_LAST_UPDATE,id));
        final int updateId = JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
        final long timestamp = JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("date").getAsNumber().longValue();
        return new PluginUpdate(updateId, timestamp);
    }

    public int getDownloadId(final long id) throws PluginRemovedException {
        final String response = getHttp(String.format(API_RESOURCE_DETAILS,id));
        final JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        if(object.has("error")) {
            if(object.getAsJsonPrimitive("error").getAsString().equals("resource not found")) {
                throw new PluginRemovedException();
            }
        }
        final String downloadPath = object.getAsJsonObject("file").getAsJsonPrimitive("url").getAsString();
        final String[] split = downloadPath.split("=");
        return Integer.parseInt(split[split.length-1]);
    }

    private String getHttp(final String url) {
        waitIfNeeded();
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .header("User-Agent","DiscordSpigotUpdateBot/1.0")
                .method("GET", null)
                .build();
        try (final Response response = client.newCall(request).execute();
             final ResponseBody body = Objects.requireNonNull(response.body());) {
            final String responseString = body.string();
            body.close();
            response.close();
            return responseString;
        } catch (final IOException e) {
            throw new IllegalStateException("Could not get API response:",e);
        }
    }

    private void waitIfNeeded() {
        if(System.currentTimeMillis() < lastRequest + minimumRequestDelay) {
            try {
                Thread.sleep(lastRequest + minimumRequestDelay - System.currentTimeMillis());
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastRequest = System.currentTimeMillis();
    }
}
