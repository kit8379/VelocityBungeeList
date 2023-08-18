**🚀 VelocityBungeeList for BungeeCord/Velocity 🚀**

Elevate your server listing experience to the next level! Introducing VelocityBungeeList for BungeeCord, the ultimate solution to display your server list in a chic and intuitive manner.
Support RedisBungee! It menas that you can use this plugin on multiple proxies setup! (Both BungeeCord and Velocity)

🌟 Features 🌟

    📋 Get a comprehensive list of all players across your servers.
    🕹 Filter and view players specific to each server.
    🔄 Reload functionality for swift updates.
    🌐 Integrated with RedisBungeeAPI for expansive and efficient data retrieval.
    🛡 Permission-based command system ensuring server security.

📥 Installation 📥

    Download the plugin from the SpigotMC page.
    Drop the .jar file into your BungeeCord/Velocity's plugins folder.
    Restart your BungeeCord/Velocity server or load the plugin.
    Enjoy the enhanced listing experience!

📘 Commands & Permissions 📘

    /list - Display the server list. Permission: velocitybungeelist.list
    /list <server/group> - Display the player list of a specific server or group. Permission: velocitybungeelist.list
    /listreload - Reload the plugin configuration. Permission: velocitybungeelist.reload

⚙ Configuration ⚙

The plugin offers a sleek and intuitive configuration system. Easily define server groups, customize your messages, and tailor the plugin to suit your server's theme!

config.yml(default):
```yaml
server-name:
  skyblock: 'skyblock'
  survival: 'survival'

server-group:
  party:
    name: 'Parties Games'
    servers:
      - party1
      - party2
      - party3
  infect:
    name: 'Infection Games'
    servers:
      - infect1
      - infect2
      - infect3

messages:
  total-players: "&eTotal online players&f: &a%total_players%"
  server-group-format: "&r%group_name%&r&f: &f%player_count% &f[%player_names%]"
  server-format: "&r%server_name%&r&f: &f%player_count% &7[%player_names%]"
  reload: '&aVelocityList is reloaded.'
  no-group-or-server: '&cCannnot find this server.'
  no-permission: '&cNo permission'
```

🚧 Support & Issues 🚧

Facing issues or need help? Reach out on the SpigotMC discussion thread or file an issue on our GitHub page.

📜 License 📜

Licensed under the MIT License. Feel free to modify and distribute as you see fit, but remember to give credit!

❤ Special Thanks ❤


