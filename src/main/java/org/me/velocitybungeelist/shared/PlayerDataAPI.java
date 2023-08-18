package org.me.velocitybungeelist.shared;

import java.util.Set;
import java.util.UUID;

public interface PlayerDataAPI {
    int getTotalPlayerCount();
    Set<UUID> getPlayersOnServer(String server);
    String getNameFromUuid(UUID uuid);
}
