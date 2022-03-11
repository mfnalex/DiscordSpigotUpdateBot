package com.jeff_media.discordspigotupdatebot.discord;

import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.config.DiscordConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class DiscordManager {

    private final JDA jda;
    private final String channelId;

    public DiscordManager() {
        final DiscordConfig discordConfig = new DiscordConfig();
        try {
            jda = JDABuilder.createDefault(discordConfig.getBotToken()).build();
            jda.awaitReady();
        } catch (final LoginException | InterruptedException e) {
            throw new IllegalStateException("Could not login to Discord, check your bot-token in discord.yml",e);
        }
        channelId = discordConfig.getChannelId();
        if(getChannel() == null) {
            jda.shutdown();
            throw new IllegalStateException("Could not find message channel, check your channel-id in discord.yml (current value: " + channelId+")");
        }
    }

    private MessageChannel getChannel() {
        return jda.getChannelById(MessageChannel.class, channelId);
    }

    public void sendUpdateEmbed(final Plugin plugin) {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Update released: " + plugin.name() + " " + plugin.version(), null);
        eb.setColor(Color.ORANGE);
        eb.setDescription("A new update has been released for " + getLink(plugin.getSpigotLink(),plugin.name()) + ". Please update soon.");
        eb.addField("Plugin",plugin.name(),true);
        eb.addField("New version",plugin.version(), true);
        eb.addField("Date",getTime(plugin.timestamp()),true);
        //eb.addBlankField(true);
        eb.addField("SpigotMC Link",getLink(plugin.getSpigotLink()), true);
        eb.addField("Changelog",getLink(plugin.getUpdateLink()), true);
        eb.addField("Download",getLink(plugin.getDownloadLink()), true);

        if(plugin.thumbnail() != null) {
            eb.setThumbnail(plugin.thumbnail());
        }

        final MessageChannel channel = getChannel();
        if(channel == null) {
            throw new IllegalStateException("Channel does not exist anymore");
        }
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    private static String getLink(final String url) {
        return getLink(url, "Click here");
    }

    private static String getLink(final String url, final String title) {
        return "[" + title + "](" + url + ")";
    }

    private static String getTime(final long timestamp) {
        return "<t:" + timestamp + ">";
    }
}
