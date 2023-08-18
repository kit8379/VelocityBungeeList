package org.me.velocitybungeelist.shared;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

import java.util.Set;
import java.util.UUID;

public class RedisPlayerDataAPI implements PlayerDataAPI {
    private final RedisBungeeAPI api;

    public RedisPlayerDataAPI(RedisBungeeAPI api) {
        this.api = api;
    }

    @Override
    public int getTotalPlayerCount() {
        return api.getPlayerCount();
    }

    @Override
    public Set<UUID> getPlayersOnServer(String server) {
        return api.getPlayersOnServer(server);
    }

    @Override
    public String getNameFromUuid(UUID uuid) {
        return api.getNameFromUuid(uuid);
    }
}
