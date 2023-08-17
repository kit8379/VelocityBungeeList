package org.me.velocitylist.command;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import org.me.velocitylist.ConfigHelper;
import org.me.velocitylist.VelocityList;

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

        if (!source.hasPermission("synccommand.list")) {
            source.sendMessage(Component.text(config.getNoPermissionMessage()));
            return;
        }

        RedisBungeeAPI api = plugin.getApi();

        // Display total player count
        int totalPlayers = api.getPlayerCount();
        source.sendMessage(Component.text(config.getTotalPlayersMessage() + totalPlayers));

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
            source.sendMessage(Component.text(groupName + ": " + totalPlayersInGroup + " [" + playerNamesInGroup.toString().replaceAll(", $", "") + "]"));
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
                source.sendMessage(Component.text(serverDisplayName + ": " + playersOnServer.size() + " [" + playerNames.toString().replaceAll(", $", "") + "]"));
            }
        }
    }
}
