package org.me.bungeevelocitylist.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.me.bungeevelocitylist.shared.ConfigHelper;
import org.me.bungeevelocitylist.bungee.BungeeList;

public class ReloadCommand extends Command {

    private final BungeeList plugin;
    private final ConfigHelper config;

    public ReloadCommand(BungeeList plugin, ConfigHelper config) {
        super("listreload");  // The command name
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || !sender.hasPermission("bungeevelocitylist.reload")) {
            sender.sendMessage(new TextComponent(config.getNoPermissionMessage()));
            return;
        }

        plugin.reload();
        sender.sendMessage(new TextComponent(config.getReloadMessage()));
    }
}
