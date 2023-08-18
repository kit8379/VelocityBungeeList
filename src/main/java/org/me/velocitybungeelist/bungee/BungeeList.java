package org.me.velocitybungeelist.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import org.me.velocitybungeelist.bungee.command.ListCommand;
import org.me.velocitybungeelist.bungee.command.ReloadCommand;
import org.me.velocitybungeelist.shared.ConfigHelper;

import java.util.logging.Logger;

public class BungeeList extends Plugin {

    private Logger logger;
    private ProxyServer proxy;
    private RedisBungeeAPI redisBungeeAPI;

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

    public void initialize() {
        this.redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();
        ConfigHelper configHelper = new ConfigHelper(logger);
        configHelper.loadConfiguration();
        proxy.getPluginManager().registerCommand(this, new ListCommand(this, configHelper));
        proxy.getPluginManager().registerCommand(this, new ReloadCommand(this, configHelper));
    }

    public void shutdown() {
    }

    public void reload() {
        getLogger().info("BungeeList is reloading...");
        shutdown();
        onEnable();
        getLogger().info("BungeeList has reloaded successfully!");
    }

    public RedisBungeeAPI getRedisBungeeAPI() {
        return this.redisBungeeAPI;
    }
}
