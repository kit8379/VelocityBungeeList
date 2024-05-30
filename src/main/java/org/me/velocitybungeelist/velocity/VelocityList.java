package org.me.velocitybungeelist.velocity;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.me.velocitybungeelist.shared.ConfigHelper;
import org.me.velocitybungeelist.shared.PlayerDataAPI;
import org.me.velocitybungeelist.shared.RedisPlayerDataAPI;
import org.me.velocitybungeelist.velocity.command.ListCommand;
import org.me.velocitybungeelist.velocity.command.ReloadCommand;

import javax.inject.Inject;
import java.util.logging.Logger;

@Plugin(id = "velocitylist", name = "VelocityList", version = "1.0", description = "A plugin to show server list", authors = {"kit8379"}, dependencies = {@Dependency(id = "redisbungee", optional = true)})
public class VelocityList {

    private final Logger logger;
    private final ProxyServer proxy;

    @Inject
    public VelocityList(Logger logger, ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("VelocityList is starting up...");
        initialize();
        logger.info("VelocityList has started successfully!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("VelocityList is shutting down...");
        shutdown();
        logger.info("VelocityList has shut down successfully!");
    }

    public void initialize() {
        PlayerDataAPI dataAPI;
        try {
            RedisBungeeAPI redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();
            dataAPI = new RedisPlayerDataAPI(redisBungeeAPI);
            logger.info("RedisBungee detected and API initialized.");
        } catch (Exception e) {
            dataAPI = new VelocityPlayerDataAPI(this);
            logger.warning("RedisBungee not found or initialization failed. Falling back to VelocityPlayerDataAPI.");
        }

        ConfigHelper configHelper = new ConfigHelper(logger);
        configHelper.loadConfiguration();
        proxy.getCommandManager().register("list", new ListCommand(this, configHelper, dataAPI));
        proxy.getCommandManager().register("listreload", new ReloadCommand(this, configHelper));
    }

    public void shutdown() {
    }

    public void reload() {
        logger.info("VelocityList is reloading...");
        shutdown();
        initialize();
        logger.info("VelocityList has reloaded successfully!");
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }
}
