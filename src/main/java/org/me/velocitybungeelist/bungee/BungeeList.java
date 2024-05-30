package org.me.velocitybungeelist.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import org.me.velocitybungeelist.bungee.command.ListCommand;
import org.me.velocitybungeelist.bungee.command.ReloadCommand;
import org.me.velocitybungeelist.shared.ConfigHelper;
import org.me.velocitybungeelist.shared.PlayerDataAPI;
import org.me.velocitybungeelist.shared.RedisPlayerDataAPI;

import java.util.logging.Logger;

public class BungeeList extends Plugin {

    private Logger logger;
    private ProxyServer proxy;

    @Override
    public void onEnable() {
        this.logger = getLogger();
        this.proxy = getProxy();
        logger.info("BungeeList is starting up...");
        initialize();
        logger.info("BungeeList has started successfully!");
    }

    @Override
    public void onDisable() {
        logger.info("BungeeList is shutting down...");
        shutdown();
        logger.info("BungeeList has shut down successfully!");
    }

    private void initialize() {
        PlayerDataAPI dataAPI;
        try {
            RedisBungeeAPI redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();
            dataAPI = new RedisPlayerDataAPI(redisBungeeAPI);
            logger.info("RedisBungee detected and API initialized.");
        } catch (Exception e) {
            dataAPI = new BungeePlayerDataAPI();
            logger.warning("RedisBungee not found or initialization failed. Falling back to BungeePlayerDataAPI.");
        }

        ConfigHelper configHelper = new ConfigHelper(logger);
        configHelper.loadConfiguration();
        proxy.getPluginManager().registerCommand(this, new ListCommand(configHelper, dataAPI));
        proxy.getPluginManager().registerCommand(this, new ReloadCommand(this, configHelper));
    }

    public void shutdown() {
    }

    public void reload() {
        getLogger().info("BungeeList is reloading...");
        shutdown();
        initialize();
        getLogger().info("BungeeList has reloaded successfully!");
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }
}
