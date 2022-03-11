package com.jeff_media.discordspigotupdatebot.discord.embed;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(fluent = true)
public class Field {
    private final String name;
    private final String text;
    private final boolean inline;

    public boolean isBlank() {
        return name.equals("") && text.equals("");
    }

    public static Field deserialize(final Map<String,Object> map) {
        if(!map.containsKey("name") || !map.containsKey("text") || !map.containsKey("inline")) {
            throw new IllegalArgumentException("Fields must contain \"name\" and \"text\" and \"inline\". For blank fields, set \"name\" and \"text\" to an empty string (\"\")");
        }
        String name = (String) map.get("name");
        String text = (String) map.get("text");
        if(name == null) name = "";
        if(text == null) text = "";
        final boolean inline = (boolean) map.get("inline");
        return new Field(name, text, inline);
    }
}
