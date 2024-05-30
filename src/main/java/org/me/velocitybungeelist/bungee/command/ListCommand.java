package org.me.velocitybungeelist.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import org.me.velocitybungeelist.shared.ConfigHelper;
import org.me.velocitybungeelist.shared.PlayerDataAPI;
import org.me.velocitybungeelist.shared.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListCommand extends Command {

    private final PlayerDataAPI dataAPI;
    private final ConfigHelper config;

    public ListCommand(ConfigHelper config, PlayerDataAPI dataAPI) {
        super("list");
        this.config = config;
        this.dataAPI = dataAPI;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("velocitybungeelist.list")) {
            sendNoPermissionMessage(sender);
            return;
        }

        if (args.length > 0) {
            String target = args[0];
            if (config.getServerGroups().containsKey(target)) {
                displayGroupPlayers(sender, target);
                return;
            } else if (ProxyServer.getInstance().getServerInfo(target) != null) {
                displayServerPlayers(sender, target);
                return;
            } else {
                sendNoGroupOrServerMessage(sender, target);
                return;
            }
        }

        displayTotalPlayerCount(sender);
        displayGroupAndServerPlayers(sender);
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage(new TextComponent(Utils.colorize(config.getNoPermissionMessage())));
    }

    private void sendNoGroupOrServerMessage(CommandSender sender, String target) {
        sender.sendMessage(new TextComponent(Utils.colorize(config.getNoGroupOrServerMessage().replace("%target%", target))));
    }

    private void displayTotalPlayerCount(CommandSender sender) {
        int totalPlayers = dataAPI.getTotalPlayerCount();
        String totalPlayersMessage = config.getTotalPlayersMessage().replace("%total_players%", String.valueOf(totalPlayers));
        sender.sendMessage(new TextComponent(Utils.colorize(totalPlayersMessage)));
    }

    private void displayGroupAndServerPlayers(CommandSender sender) {
        Set<String> serversInGroups = new HashSet<>();

        config.getServerGroups().forEach((groupKey, groupNode) -> {
            String message = getPlayerGroupMessage(groupKey.toString());
            sender.sendMessage(new TextComponent(Utils.colorize(message)));
            serversInGroups.addAll(config.getServersInGroup(groupKey.toString()));
        });

        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            if (!serversInGroups.contains(server.getName())) {
                String message = getServerMessage(server.getName());
                sender.sendMessage(new TextComponent(Utils.colorize(message)));
            }
        }
    }

    private String getPlayerGroupMessage(String groupKey) {
        List<String> servers = config.getServersInGroup(groupKey);
        int groupPlayerCount = servers.stream().mapToInt(server -> dataAPI.getPlayersOnServer(server).size()).sum();

        String playerNames = servers.stream().flatMap(server -> dataAPI.getPlayersOnServer(server).stream()).map(dataAPI::getNameFromUuid).collect(Collectors.joining(", "));

        String groupName = config.getServerGroupName(groupKey).orElse(groupKey);
        return formatMessage(config.getServerGroupFormat(), groupName, groupPlayerCount, playerNames);
    }

    private String getServerMessage(String server) {
        Set<UUID> playersOnServer = dataAPI.getPlayersOnServer(server);
        String playerNames = playersOnServer.stream().map(dataAPI::getNameFromUuid).collect(Collectors.joining(", "));

        String serverName = config.getServerName(server).orElse(server);
        return formatMessage(config.getServerFormat(), serverName, playersOnServer.size(), playerNames);
    }

    private String formatMessage(String template, String name, int playerCount, String playerNames) {
        return template.replace("%server_name%", Utils.colorize(name)).replace("%group_name%", Utils.colorize(name)).replace("%player_count%", String.valueOf(playerCount)).replace("%player_names%", playerNames.replaceAll(", $", ""));
    }

    private void displayGroupPlayers(CommandSender sender, String groupKey) {
        sender.sendMessage(new TextComponent(getPlayerGroupMessage(groupKey)));
    }

    private void displayServerPlayers(CommandSender sender, String serverKey) {
        sender.sendMessage(new TextComponent(getServerMessage(serverKey)));
    }
}
