package org.me.velocitybungeelist.velocity;

import com.velocitypowered.api.proxy.Player;
import org.me.velocitybungeelist.shared.PlayerDataAPI;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class VelocityPlayerDataAPI implements PlayerDataAPI {

    private final VelocityList plugin;

    public VelocityPlayerDataAPI(VelocityList plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getTotalPlayerCount() {
        return plugin.getProxy().getAllPlayers().size();
    }

    @Override
    public Set<UUID> getPlayersOnServer(String server) {
        return plugin.getProxy().getServer(server)
                .map(serverInfo -> serverInfo.getPlayersConnected().stream().map(Player::getUniqueId).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    @Override
    public String getNameFromUuid(UUID uuid) {
        return plugin.getProxy().getPlayer(uuid).map(Player::getUsername).orElse(null);
    }
}

