package org.me.velocitylist;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class ConfigHelper {

    private final Logger logger;
    private final Path dataFolder;
    private ConfigurationNode configData;

    public ConfigHelper(Logger logger) {
        this.logger = logger;
        this.dataFolder = Path.of("plugins/VelocityList");
    }

    public void loadConfiguration() {
        try {
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }

            Path configFile = dataFolder.resolve("config.yml");
            YamlConfigurationLoader loader =
                    YamlConfigurationLoader.builder()
                            .path(configFile)
                            .nodeStyle(NodeStyle.BLOCK)
                            .build();

            if (!Files.exists(configFile)) {
                // Copying the default config from resources
                try (InputStream defaultConfigStream = this.getClass().getResourceAsStream("/config.yml")) {
                    if (defaultConfigStream != null) {
                        Files.copy(defaultConfigStream, configFile);
                    } else {
                        throw new IOException("Could not find default config in resources!");
                    }
                }
            }
            configData = loader.load();
        } catch (IOException e) {
            logger.warning("Failed to load config.yml: " + e.getMessage());
        }
    }

    public Optional<String> getServerName(String key) {
        return Optional.ofNullable(configData.node("server-name", key).getString());
    }

    public Map<Object, ? extends ConfigurationNode> getServerGroups() {
        return configData.node("server-group").childrenMap();
    }

    public Optional<String> getServerGroupName(String groupKey) {
        return Optional.ofNullable(configData.node("server-group", groupKey, "name").getString());
    }

    public List<String> getServersInGroup(String groupKey) {
        try {
            return configData.node("server-group", groupKey, "servers").getList(String.class);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTotalPlayersMessage() {
        return Utils.colorize(configData.node("messages", "totalPlayers").getString("&aTotal players online: "));
    }

    public String getReloadMessage() {
        return Utils.colorize(configData.node("messages", "reload").getString("&aVelocityList has been reloaded."));
    }

    public String getNoPermissionMessage() {
        return Utils.colorize(configData.node("messages", "noPermission").getString("&cYou do not have permission to use this command."));
    }
}
