package com.jeff_media.discordspigotupdatebot.discord.embed;

import com.jeff_media.discordspigotupdatebot.data.Plugin;
import com.jeff_media.discordspigotupdatebot.util.MessageUtils;
import com.jeff_media.discordspigotupdatebot.util.YamlUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Embed {

    private final String title;
    private final String description;
    private final List<Field> fields;
    private final String thumbnail;
    private final String footer;
    private final String footerIcon;

    private static Embed deserialize(final Map<String,Object> map) {
        final String title = (String) map.get("title");
        String description = null;
        if(map.containsKey("description")) {
            final Object descriptionObject = map.get("description");
            if(descriptionObject != null) {
                if (descriptionObject instanceof String) {
                    description = (String) descriptionObject;
                } else if (descriptionObject instanceof List<?>) {
                    ((List<?>) descriptionObject).stream().forEach(line -> {
                        if (!(line instanceof String))
                            throw new IllegalArgumentException("Invalid string found in embed description");
                    });
                    description = ((List<?>) descriptionObject).stream().map(o -> (String) o).collect(Collectors.joining(System.lineSeparator()));
                } else {
                    throw new IllegalArgumentException("Embed description must be a string or a list of strings");
                }
            }
        }
        final List<Field> fields = new ArrayList<>();
        if(map.containsKey("fields")) {
            final List<Map<String,Object>> fieldList = (List<Map<String,Object>>) map.get("fields");
            if(fieldList != null) {
                for (final Map<String, Object> fieldObject : fieldList) {
                    final Field field = Field.deserialize(fieldObject);
                    fields.add(field);
                }
            }
        }
        final String thumbnail = (String) map.get("thumbnail");
        final String footer = (String) map.get("footer");
        final String footerIcon = (String) map.get("footer-icon");
        return new Embed(title, description, fields, thumbnail, footer, footerIcon);
    }

    @RequiredArgsConstructor
    public static class Builder {

        private static final Map<String,Object> YAML = YamlUtils.loadFile("embed.yml");
        private static final Map<String,Object> YAML_WARNING = YamlUtils.loadFile("embed-warning.yml");
        private static final Embed EMBED = Embed.deserialize(YAML);
        private static final Embed EMBED_WARNING = Embed.deserialize(YAML_WARNING);
        
        private final Plugin plugin;

        private String applyPlaceholders(final String text) {
            return MessageUtils.applyPlaceholders(plugin, text);
        }
        
        public MessageEmbed buildWarning() {
            return buildEmbed(EMBED_WARNING);
        }

        public MessageEmbed build() {
            return buildEmbed(EMBED);
        }

        private MessageEmbed buildEmbed(Embed embed) {
            final EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(applyPlaceholders(embed.title));
            builder.setDescription(applyPlaceholders(embed.description));
            for (final Field field : embed.fields) {
                if (field.isBlank()) {
                    builder.addBlankField(field.inline());
                } else {
                    builder.addField(applyPlaceholders(field.name()), applyPlaceholders(field.text()), field.inline());
                }
            }

            builder.setThumbnail(applyPlaceholders(embed.thumbnail));
            builder.setFooter(applyPlaceholders(embed.footer), applyPlaceholders(embed.footerIcon));

            return builder.build();
        }
    }


}
