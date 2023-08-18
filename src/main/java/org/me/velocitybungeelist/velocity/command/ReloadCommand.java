package org.me.velocitybungeelist.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import org.me.velocitybungeelist.shared.ConfigHelper;
import org.me.velocitybungeelist.velocity.VelocityList;

public class ReloadCommand implements SimpleCommand {

    private final VelocityList plugin;
    private final ConfigHelper config;

    public ReloadCommand(VelocityList plugin, ConfigHelper config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!source.hasPermission("bungeevelocitylist.reload")) {
            source.sendMessage(Component.text(config.getNoPermissionMessage()));
            return;
        }

        plugin.reload();
        source.sendMessage(Component.text(config.getReloadMessage()));
    }
}
