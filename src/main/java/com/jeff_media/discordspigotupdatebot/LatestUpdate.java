package com.jeff_media.discordspigotupdatebot;

import java.util.Base64;

public record LatestUpdate(String title, String version, int id, String changelogBase64) {

    public String getChangelog() {
        return new String(Base64.getDecoder().decode(changelogBase64));
    }
}
