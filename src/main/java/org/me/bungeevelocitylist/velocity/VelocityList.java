package org.me.bungeevelocitylist.velocity;

import javax.inject.Inject;
import java.util.logging.Logger;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import org.me.bungeevelocitylist.velocity.command.ListCommand;
import org.me.bungeevelocitylist.velocity.command.ReloadCommand;
import org.me.bungeevelocitylist.shared.ConfigHelper;

@Plugin(
        id = "velocitylist",
        name = "VelocityList",
        version = "1.0",
        description = "A plugin to show server list",
        authors = {"TonyPak"},
        dependencies = {
                @Dependency(id = "redisbungee")
        }
)
public class VelocityList {

    private final Logger logger;
    private final ProxyServer proxy;
    private RedisBungeeAPI api;

    @Inject
    public VelocityList(Logger logger, ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        initialize();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        shutdown();
    }

    public void initialize() {
        this.api = RedisBungeeAPI.getRedisBungeeApi();
        ConfigHelper configHelper = new ConfigHelper(logger);
        configHelper.loadConfiguration();
        proxy.getCommandManager().register("list", new ListCommand(this, configHelper));
        proxy.getCommandManager().register("listreload", new ReloadCommand(this, configHelper));
    }

    public void shutdown() {
        // Shutdown the plugin
    }

    public void reload() {
        logger.info("VelocityList is reloading...");
        shutdown();
        initialize();
        logger.info("VelocityList has reloaded successfully!");
    }

    public RedisBungeeAPI getApi() {
        return this.api;
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }
}
