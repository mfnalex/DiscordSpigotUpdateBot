package com.jeff_media.discordspigotupdatebot.spiget;

import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class SpigetAPI {

    private static final String API_LAST_UPDATE = "https://api.spiget.org/v2/resources/%d/updates/latest";
    private static final String API_LAST_VERSION = "https://api.spiget.org/v2/resources/%d/versions/latest";
    private static final String API_RESOURCE_DETAILS = "https://api.spiget.org/v2/resources/%d";

    public static String getVersion(long id) {
        String response = getHttp(String.format(API_LAST_VERSION,id));
        return JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
    }

    public static int getUpdateId(long id) {
        String response = getHttp(String.format(API_LAST_UPDATE,id));
        return JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
    }

    public static int getDownloadId(long id) {
        String response = getHttp(String.format(API_RESOURCE_DETAILS,id));
        String downloadPath = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("file").getAsJsonPrimitive("url").getAsString();
        String[] split = downloadPath.split("=");
        return Integer.parseInt(split[split.length-1]);
    }

    private static String getHttp(String url) {
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
}
