package com.jeff_media.discordspigotupdatebot;

import com.google.gson.*;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.yaml.snakeyaml.Yaml;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordSpigotUpdateBot {

    @Getter private static DiscordSpigotUpdateBot instance;
    @Getter private final JDA jda;
    @Getter private Map<Integer,LatestUpdate> versions;
    private final String channelID;
    private static final String AUTHOR_ID = "175238";
    private static final String API_ALL_RESOURCES = "https://api.spiget.org/v2/authors/%s/resources?size=50";
    private static final String API_LAST_UPDATE = "https://api.spiget.org/v2/resources/%d/updates/latest";
    private static final String API_LAST_VERSION = "https://api.spiget.org/v2/resources/%d/versions/latest";
    private static final String API_RESOURCE_DETAILS = "https://api.spiget.org/v2/resources/%d";

    public DiscordSpigotUpdateBot(String botToken, String channelId) {
        instance = this;
        this.channelID = channelId;
        try {
            jda = JDABuilder.createDefault(botToken).build();
        } catch (LoginException e) {
            throw new IllegalArgumentException("Wrong bot token");
        }
        getInitialVersions();
        while(true) {
            checkForUpdates();
        }
    }

    private void checkForUpdates() {
        List<Integer> updatedPlugins = getUpdatedPlugins();
        for(int pluginId : updatedPlugins) {
            sleep(2);
            String newVersion = versions.get(pluginId).title();
            String response = getHttp(String.format(API_RESOURCE_DETAILS,pluginId));
            if(response == null) {
                throw new IllegalArgumentException("Response for plugin details returned null");
            }
            JsonObject plugin = JsonParser.parseString(response).getAsJsonObject();
            String url = "https://spigotmc.org/resources/" + pluginId;
            String directDownload = "https://spigotmc.org/" + plugin.get("file").getAsJsonObject().getAsJsonPrimitive("url").getAsString();
            String changelogUrl = "https://spigotmc.org/resources/" + pluginId + "/update?update=" + versions.get(pluginId).id();
            String pluginName = plugin.getAsJsonPrimitive("name").getAsString();
            //String iconUrl = "https://spigotmc.org/" + plugin.getAsJsonObject("icon").getAsJsonPrimitive("url").getAsString();
            System.out.println("Plugin Update: ");
            System.out.println("  Name: " + pluginName);
            System.out.println("  Version: " + newVersion);
            System.out.println("  Download: " + directDownload);
            System.out.println("  SpigotMC: " + url);
            sendUpdateEmbed(Plugin.fromId(pluginId),versions.get(pluginId),url,directDownload,changelogUrl);
        }
        sleep(60);
    }

    public static void main(String[] args) {

        File configYml = new File("config.yml");
        if(!configYml.exists()) {
            System.out.println("No config.yml file found at " + configYml.getAbsolutePath());
            System.out.println("Please create a config.yml and insert the following values:");
            System.out.println("  bot-token: \"your-bot-token\"");
            System.out.println("  channel: \"your-channel-id\"");
            return;
        }

        Map<String,Object> config;
        try (InputStreamReader reader = new FileReader(configYml)){
            config = new Yaml().load(reader);
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        if(!config.containsKey("bot-token")) {
            throw new IllegalArgumentException("bot-token not set in config.yml");
        }
        if(!config.containsKey("channel")) {
            throw new IllegalArgumentException("channel not set in config.yml");
        }

        new DiscordSpigotUpdateBot((String) config.get("bot-token"), (String) config.get("channel"));
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not sleep");
        }
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
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }

    private List<Integer> getUpdatedPlugins() {
        Map<Integer,LatestUpdate> newVersions = getVersions();
        List<Integer> updates = new ArrayList<>();
        for(int plugin : newVersions.keySet()) {
            if(!versions.get(plugin).equals(newVersions.get(plugin))) {
                updates.add(plugin);
            }
        }
        versions = newVersions;
        return updates;
    }

    private LatestUpdate getLatestVersion(int resource) {
        String response = getHttp(String.format(API_LAST_UPDATE,resource));
        if(response == null) {
            throw new IllegalArgumentException("Could not get latest update for resource " + resource);
        }
        String title = JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("title").getAsString();
        int id = JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
        String changelogBase64 = JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("description").getAsString();
        sleep(1);
        response = getHttp(String.format(API_LAST_VERSION,resource));
        if(response == null) {
            throw new IllegalArgumentException("Could not get latest version for resource " + resource);
        }
        String version = JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
        return new LatestUpdate(title, version, id, changelogBase64);
    }

    private void getInitialVersions() {
        System.out.println("Getting initial resource list...");
        versions = getVersions();
        /*for(int plugin : versions.keySet()) {
            versions.put(plugin, new LatestUpdate("0","0",0,""));
        }*/
        System.out.println("Loaded " + versions.size() + " plugins.");
    }

    private Map<Integer,LatestUpdate> getVersions() {
        Map<Integer,LatestUpdate> versions = new HashMap<>();
        String api = String.format(API_ALL_RESOURCES,AUTHOR_ID);
        String response = getHttp(api);
        if(response == null) {
            throw new IllegalArgumentException("Resource list returned null");
        }
        JsonArray array = JsonParser.parseString(response).getAsJsonArray();
        for(JsonElement element : array) {
            sleep(1);
            int resourceId = element.getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
            LatestUpdate latestUpdate = getLatestVersion(resourceId);
            String latestVersion = latestUpdate.title();
            System.out.println(resourceId + ": " + latestVersion);
            versions.put(resourceId, latestUpdate);
        }
        return versions;
    }

    private void sendUpdateEmbed(Plugin plugin, LatestUpdate latestUpdate, String spigotUrl, String downloadUrl, String changelogUrl) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Update released: " + plugin.getName() + " " + latestUpdate.version(), null);
        eb.setColor(Color.ORANGE);
        eb.setDescription("A new update has been released for " + plugin.getName() + ". Please update soon.");
        //eb.addBlankField(false);
        eb.addField("Plugin",plugin.getName(),true);
        eb.addField("New version",latestUpdate.version(), true);
        eb.addBlankField(true);
        //eb.addBlankField(false);
        //eb.addField("Changelog",latestUpdate.getChangelog(),false);
        //eb.addBlankField(false);
        eb.addField("SpigotMC Link",getLink(spigotUrl), true);
        eb.addField("Changelog",getLink(changelogUrl), true);
        eb.addField("Download",getLink(downloadUrl), true);

        if(plugin.getLogo() != null) {
            eb.setThumbnail(plugin.getLogo());
        }

        jda.getTextChannelById(channelID).sendMessageEmbeds(eb.build()).queue();
    }

    private static String getLink(String url) {
        return "[Click here](" + url + ")";
    }
}
