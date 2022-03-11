A Discord Bot to announce SpigotMC plugin updates on your Discord server. It's meant for plugin developers but can of course also be used by server admins to get notified about new updates.

It uses Spiget.org to fetch version information.

## Usage
Build a .jar using `mvn package`, then start it using `java -jar discord-spigot-update-bot.jar`. You need at least Java 17. The bot will generate three config files. Adjust at least your `bot-token` and `channel-id` in the spigot.yml file. You can now add plugins to the plugins.yml file, then start the bot again.

## Screenshots
![](https://static.jeff-media.com/img/discord-spigot-update-bot/discord-spigot-update-bot-screenshot1.png)