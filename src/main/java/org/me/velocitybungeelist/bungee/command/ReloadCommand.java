package org.me.velocitybungeelist.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.me.velocitybungeelist.bungee.BungeeList;
import org.me.velocitybungeelist.shared.ConfigHelper;

public class ReloadCommand extends Command {

    private final BungeeList plugin;
    private final ConfigHelper config;

    public ReloadCommand(BungeeList plugin, ConfigHelper config) {
        super("reload");
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("velocitybungeelist.reload")) {
            sender.sendMessage(new TextComponent(config.getNoPermissionMessage()));
            return;
        }

        plugin.reload();
        sender.sendMessage(new TextComponent(config.getReloadMessage()));
    }
}
