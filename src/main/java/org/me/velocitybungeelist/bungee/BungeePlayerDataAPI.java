package org.me.velocitybungeelist.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.me.velocitybungeelist.shared.PlayerDataAPI;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeePlayerDataAPI implements PlayerDataAPI {

    public BungeePlayerDataAPI() {
    }

    @Override
    public int getTotalPlayerCount() {
        return ProxyServer.getInstance().getPlayers().size();
    }

    @Override
    public Set<UUID> getPlayersOnServer(String server) {
        return ProxyServer.getInstance().getServerInfo(server)
                .getPlayers().stream()
                .map(ProxiedPlayer::getUniqueId)
                .collect(Collectors.toSet());
    }

    @Override
    public String getNameFromUuid(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        return (player != null) ? player.getName() : null;
    }
}
