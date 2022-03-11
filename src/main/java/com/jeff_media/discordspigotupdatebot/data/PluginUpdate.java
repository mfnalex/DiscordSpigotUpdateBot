package com.jeff_media.discordspigotupdatebot.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class PluginUpdate {
    private final int updateId;
    private final long timestamp;
}
