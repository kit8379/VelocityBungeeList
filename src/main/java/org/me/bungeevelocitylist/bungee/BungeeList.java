package org.me.bungeevelocitylist.bungee;

import java.util.logging.Logger;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.ProxyServer;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import org.me.bungeevelocitylist.bungee.command.ListCommand;
import org.me.bungeevelocitylist.bungee.command.ReloadCommand;
import org.me.bungeevelocitylist.shared.ConfigHelper;

public class BungeeList extends Plugin{

    private Logger logger;
    private ProxyServer proxy;
    private RedisBungeeAPI api;

    @Override
    public void onEnable() {
        this.logger = this.getLogger();
        this.proxy = this.getProxy();
        initialize();
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    public void initialize() {
        this.api = RedisBungeeAPI.getRedisBungeeApi();
        ConfigHelper configHelper = new ConfigHelper(logger);
        configHelper.loadConfiguration();

        PluginManager pm = proxy.getPluginManager();
        pm.registerCommand(this, new ListCommand(this, configHelper));
        pm.registerCommand(this, new ReloadCommand(this, configHelper));
    }

    public void shutdown() {
        // Shutdown the plugin
    }

    public void reload() {
        logger.info("BungeeList is reloading...");
        shutdown();
        initialize();
        logger.info("BungeeList has reloaded successfully!");
    }

    public RedisBungeeAPI getApi() {
        return this.api;
    }

    public ProxyServer getProxyServer() {
        return this.proxy;
    }
}
