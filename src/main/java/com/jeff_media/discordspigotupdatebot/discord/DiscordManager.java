package com.jeff_media.discordspigotupdatebot.discord;

import com.jeff_media.discordspigotupdatebot.DiscordSpigotUpdateBot;
import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.config.DiscordConfig;
import com.jeff_media.discordspigotupdatebot.discord.embed.Embed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.security.auth.login.LoginException;

public class DiscordManager {

    private final JDA jda;
    private final String channelId;
    private static final DiscordSpigotUpdateBot main = DiscordSpigotUpdateBot.getInstance();

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

    public void sendWarningEmbed(final Plugin plugin) {
        final MessageEmbed embed = new Embed.Builder(plugin).buildWarning();
        final MessageChannel channel = getChannel();
        if(channel == null) {
            throw new IllegalStateException("Channel does not exist anymore");
        }
        channel.sendMessageEmbeds(embed).queue();
    }

    public void sendUpdateEmbed(final Plugin plugin) {
        final MessageEmbed embed = new Embed.Builder(plugin).build();
        final MessageChannel channel = getChannel();
        if(channel == null) {
            throw new IllegalStateException("Channel does not exist anymore");
        }
        channel.sendMessageEmbeds(embed).queue();
    }

}
