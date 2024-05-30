package org.me.velocitybungeelist.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.myzelyam.api.vanish.VelocityVanishAPI;
import net.kyori.adventure.text.Component;
import org.me.velocitybungeelist.shared.ConfigHelper;
import org.me.velocitybungeelist.shared.PlayerDataAPI;
import org.me.velocitybungeelist.shared.Utils;
import org.me.velocitybungeelist.velocity.VelocityList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListCommand implements SimpleCommand {

    private final VelocityList plugin;
    private final PlayerDataAPI dataAPI;
    private final ConfigHelper config;
    private final VelocityVanishAPI vanishAPI;

    public ListCommand(VelocityList plugin, ConfigHelper config, PlayerDataAPI dataAPI, VelocityVanishAPI vanishAPI) {
        this.plugin = plugin;
        this.config = config;
        this.dataAPI = dataAPI;
        this.vanishAPI = vanishAPI;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("bungeevelocitylist.list")) {
            sendNoPermissionMessage(source);
            return;
        }

        if (args.length > 0) {
            String target = args[0];
            if (config.getServerGroups().containsKey(target)) {
                displayGroupPlayers(source, target);
                return;
            } else if (plugin.getProxy().getServer(target).isPresent()) {
                displayServerPlayers(source, target);
                return;
            } else {
                sendNoGroupOrServerMessage(source, target);
                return;
            }
        }

        displayTotalPlayerCount(source);
        displayGroupAndServerPlayers(source);
    }

    private void sendNoPermissionMessage(CommandSource source) {
        source.sendMessage(Component.text(config.getNoPermissionMessage()));
    }

    private void sendNoGroupOrServerMessage(CommandSource source, String target) {
        source.sendMessage(Component.text(config.getNoGroupOrServerMessage().replace("%target%", target)));
    }

    @SuppressWarnings("All")
    private void displayTotalPlayerCount(CommandSource source) {
        int totalPlayers = dataAPI.getTotalPlayerCount();
        if (vanishAPI != null) {
            totalPlayers -= vanishAPI.getInvisiblePlayers().size();
        }
        String totalPlayersMessage = config.getTotalPlayersMessage().replace("%total_players%", String.valueOf(totalPlayers));
        source.sendMessage(Component.text(Utils.colorize(totalPlayersMessage)));
    }

    private void displayGroupAndServerPlayers(CommandSource source) {
        Set<String> serversInGroups = new HashSet<>();

        config.getServerGroups().forEach((groupKey, groupNode) -> {
            String message = getPlayerGroupMessage(groupKey.toString());
            source.sendMessage(Component.text(message));
            serversInGroups.addAll(config.getServersInGroup(groupKey.toString()));
        });

        plugin.getProxy().getAllServers().forEach(server -> {
            if (!serversInGroups.contains(server.getServerInfo().getName())) {
                String message = getServerMessage(server.getServerInfo().getName());
                source.sendMessage(Component.text(message));
            }
        });
    }

    private String getPlayerGroupMessage(String groupKey) {
        List<String> servers = config.getServersInGroup(groupKey);
        int groupPlayerCount = servers.stream().mapToInt(server -> dataAPI.getPlayersOnServer(server).size()).sum();
        if (vanishAPI != null) {
            groupPlayerCount -= getInvisiblePlayerCount(servers);
        }

        String playerNames = servers.stream().flatMap(server -> dataAPI.getPlayersOnServer(server).stream()).filter(this::isPlayerVisible).map(dataAPI::getNameFromUuid).collect(Collectors.joining(", "));

        String groupName = config.getServerGroupName(groupKey).orElse(groupKey);
        return formatMessage(config.getServerGroupFormat(), groupName, groupPlayerCount, playerNames);
    }

    private String getServerMessage(String server) {
        Set<UUID> playersOnServer = dataAPI.getPlayersOnServer(server).stream().filter(this::isPlayerVisible).collect(Collectors.toSet());

        String playerNames = playersOnServer.stream().map(dataAPI::getNameFromUuid).collect(Collectors.joining(", "));

        String serverName = config.getServerName(server).orElse(server);
        return formatMessage(config.getServerFormat(), serverName, playersOnServer.size(), playerNames);
    }

    private String formatMessage(String template, String name, int playerCount, String playerNames) {
        return template.replace("%server_name%", Utils.colorize(name)).replace("%group_name%", Utils.colorize(name)).replace("%player_count%", String.valueOf(playerCount)).replace("%player_names%", playerNames.replaceAll(", $", ""));
    }

    private void displayGroupPlayers(CommandSource source, String groupKey) {
        source.sendMessage(Component.text(getPlayerGroupMessage(groupKey)));
    }

    private void displayServerPlayers(CommandSource source, String serverKey) {
        source.sendMessage(Component.text(getServerMessage(serverKey)));
    }

    private int getInvisiblePlayerCount(List<String> servers) {
        return (int) servers.stream().flatMap(server -> dataAPI.getPlayersOnServer(server).stream()).filter(uuid -> !isPlayerVisible(uuid)).count();
    }

    @SuppressWarnings("All")
    private boolean isPlayerVisible(UUID uuid) {
        Player player = plugin.getProxy().getPlayer(uuid).orElse(null);
        return player == null || (vanishAPI == null || !VelocityVanishAPI.isInvisible(player));
    }
}
