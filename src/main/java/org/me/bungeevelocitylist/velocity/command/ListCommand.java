package org.me.bungeevelocitylist.velocity.command;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import org.me.bungeevelocitylist.shared.ConfigHelper;
import org.me.bungeevelocitylist.shared.Utils;
import org.me.bungeevelocitylist.velocity.VelocityList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListCommand implements SimpleCommand {

    private final VelocityList plugin;
    private final ConfigHelper config;

    public ListCommand(VelocityList plugin, ConfigHelper config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("bungeevelocitylist.list")) {
            source.sendMessage(Component.text(config.getNoPermissionMessage()));
            return;
        }

        RedisBungeeAPI api = plugin.getApi();

        // If a specific server or group is provided
        if (args.length > 0) {
            String target = args[0];
            if (config.getServerGroups().containsKey(target)) {
                displayGroupPlayers(source, target, api);
                return;
            } else if (plugin.getProxy().getServer(target).isPresent()) {
                displayServerPlayers(source, target, api);
                return;
            } else {
                source.sendMessage(Component.text(config.getNoGroupOrServerMessage().replace("%target%", target)));
                return;
            }
        }

        // Display total player count
        int totalPlayers = api.getPlayerCount();
        String totalPlayersMessage = config.getTotalPlayersMessage().replace("%total_players%", String.valueOf(totalPlayers));
        source.sendMessage(Component.text(Utils.colorize(totalPlayersMessage)));

        // Create a set to keep track of servers that are part of a group
        Set<String> serversInGroups = new HashSet<>();

        // Display players per server group (if defined in config)
        config.getServerGroups().forEach((groupKey, groupNode) -> {
            String groupName = config.getServerGroupName((String) groupKey).orElse((String) groupKey);
            List<String> serversInGroup = config.getServersInGroup((String) groupKey);
            int totalPlayersInGroup = 0;
            StringBuilder playerNamesInGroup = new StringBuilder();
            for (String server : serversInGroup) {
                Set<UUID> playersOnServer = api.getPlayersOnServer(server);
                totalPlayersInGroup += playersOnServer.size();
                playersOnServer.forEach(uuid -> playerNamesInGroup.append(api.getNameFromUuid(uuid)).append(", "));
                serversInGroups.add(server);  // Add server to the set
            }
            String groupMessage = config.getServerGroupFormat()
                    .replace("%group_name%", Utils.colorize(groupName))
                    .replace("%player_count%", String.valueOf(totalPlayersInGroup))
                    .replace("%player_names%", playerNamesInGroup.toString().replaceAll(", $", ""));
            source.sendMessage(Component.text(groupMessage));
        });

        // Retrieve all server names using Velocity
        Set<String> allServers = plugin.getProxy().getAllServers().stream()
                .map(serverInfo -> serverInfo.getServerInfo().getName())
                .collect(Collectors.toSet());

        // Display players per server, but skip servers that are part of a group
        for (String server : allServers) {
            if (!serversInGroups.contains(server)) {  // Check if server is not in a group
                Set<UUID> playersOnServer = api.getPlayersOnServer(server);
                String serverDisplayName = config.getServerName(server).orElse(server);
                StringBuilder playerNames = new StringBuilder();
                playersOnServer.forEach(uuid -> playerNames.append(api.getNameFromUuid(uuid)).append(", "));
                String serverMessage = config.getServerFormat()
                        .replace("%server_name%", Utils.colorize(serverDisplayName))
                        .replace("%player_count%", String.valueOf(playersOnServer.size()))
                        .replace("%player_names%", playerNames.toString().replaceAll(", $", ""));
                source.sendMessage(Component.text(serverMessage));
            }
        }
    }

    private void displayGroupPlayers(CommandSource source, String groupKey, RedisBungeeAPI api) {
        // Get the translated group name
        String groupName = config.getServerGroupName(groupKey).orElse(groupKey);

        // Logic to display players for a specific group
        List<String> serversInGroup = config.getServersInGroup(groupKey);
        int totalPlayersInGroup = 0;
        StringBuilder playerNamesInGroup = new StringBuilder();
        for (String server : serversInGroup) {
            Set<UUID> playersOnServer = api.getPlayersOnServer(server);
            totalPlayersInGroup += playersOnServer.size();
            playersOnServer.forEach(uuid -> playerNamesInGroup.append(api.getNameFromUuid(uuid)).append(", "));
        }
        String groupMessage = config.getServerGroupFormat()
                .replace("%group_name%", Utils.colorize(groupName))
                .replace("%player_count%", String.valueOf(totalPlayersInGroup))
                .replace("%player_names%", playerNamesInGroup.toString().replaceAll(", $", ""));
        source.sendMessage(Component.text(groupMessage));
    }

    private void displayServerPlayers(CommandSource source, String serverKey, RedisBungeeAPI api) {
        // Get the translated server name
        String serverName = config.getServerName(serverKey).orElse(serverKey);

        // Logic to display players for a specific server
        Set<UUID> playersOnServer = api.getPlayersOnServer(serverKey);
        StringBuilder playerNames = new StringBuilder();
        playersOnServer.forEach(uuid -> playerNames.append(api.getNameFromUuid(uuid)).append(", "));
        String serverMessage = config.getServerFormat()
                .replace("%server_name%", Utils.colorize(serverName))
                .replace("%player_count%", String.valueOf(playersOnServer.size()))
                .replace("%player_names%", playerNames.toString().replaceAll(", $", ""));
        source.sendMessage(Component.text(serverMessage));
    }
}
