Below are some example settings for your embed.yml file.

## Example 1
![](https://static.jeff-media.com/img/discord-spigot-update-bot/embed1.png)

```yaml
title: "Update released: %name% %version%"
description:
  - "A new update has been released for [%name%](%spigot_link%). Please update soon."
fields:
  - name: Plugin
    text: "%name%"
    inline: true
  - name: Version
    text: "%version%"
    inline: true
  - name: Date
    text: "%date%"
    inline: true
  - name: Links
    text: "[Download](%download_link%)"
    inline: true
  - name: null
    text: "[Changelog](%changelog_link%)"
    inline: true
  - name: null
    text: "[SpigotMC Page](%spigot_link%)"
    inline: true
thumbnail: "%thumbnail%"
```

## Example 2
![](https://static.jeff-media.com/img/discord-spigot-update-bot/embed2.png)

```yaml
title: "%name% has been updated!"
description:
  - "**[%name%](%spigot_link%) has been updated** to version %version%. [Download it now](%download_link%) or [read the changelog](%changelog_link%)."
thumbnail: "%thumbnail%"
footer: "Discord Spigot Update Checker by JEFF Media GbR / mfnalex"
```

## Example 3
![](https://static.jeff-media.com/img/discord-spigot-update-bot/embed3.png)

```yaml
title: "New Plugin Update!"
fields:
  - name: Plugin
    text: "%plugin% %version%"
    inline: false
  - name: Date
    text: "%date%"
    inline: false
  - name: Download
    text: "[Click here](%download_link%)"
    inline: true
  - name: Spigot Page
    text: "[Click here](%spigot_link%)"
    inline: true
  - name: Changelog
    text: "[Click here](%changelog_link%)"
    inline: true
thumbnail: "%thumbnail%"
```