package com.jeff_media.discordspigotupdatebot.discord;

import com.jeff_media.discordspigotupdatebot.Plugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.NewsChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class DiscordManager {

    private final DiscordConfig discordConfig = new DiscordConfig();
    private final JDA jda;
    private final String channelId;

    public DiscordManager() {
        try {
            jda = JDABuilder.createDefault(discordConfig.getBotToken()).build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            throw new IllegalStateException("Could not login to Discord, check your bot-token in discord.yml",e);
        }
        channelId = discordConfig.getChannelId();
        if(getChannel() == null) {
            jda.shutdown();
            throw new IllegalStateException("Could not find message channel, check your channel-id in discord.yml (current value: " + channelId+")");
        }
    }

    private MessageChannel getChannel() {
        TextChannel textChannel = jda.getTextChannelById(channelId);
        if(textChannel != null) return textChannel;
        NewsChannel newsChannel = jda.getNewsChannelById(channelId);
        if(newsChannel != null) return newsChannel;
        return null;
    }

    public void sendUpdateEmbed(Plugin plugin) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Update released: " + plugin.name() + " " + plugin.version(), null);
        eb.setColor(Color.ORANGE);
        eb.setDescription("A new updateId has been released for " + plugin.name() + ". Please update soon.");
        eb.addField("Plugin",plugin.name(),true);
        eb.addField("New version",plugin.version(), true);
        eb.addBlankField(true);
        eb.addField("SpigotMC Link",getLink(plugin.getSpigotLink()), true);
        eb.addField("Changelog",getLink(plugin.getUpdateLink()), true);
        eb.addField("Download",getLink(plugin.getDownloadLink()), true);

        if(plugin.thumbnail() != null) {
            eb.setThumbnail(plugin.thumbnail());
        }

        getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    private static String getLink(String url) {
        return "[Click here](" + url + ")";
    }
}
