package dev.thatsmybaby.command.arguments;

import dev.thatsmybaby.command.Argument;
import dev.thatsmybaby.shared.MongoDBProvider;
import dev.thatsmybaby.shared.storage.PluginClanStorage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public final class CreateArgument extends Argument {

    public CreateArgument(String name, String permission, String[] aliases, boolean async) {
        super(name, permission, aliases, async);
    }

    @Override
    public void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Usage: /clan create <name>").color(ChatColor.RED).create());

            return;
        }

        if (MongoDBProvider.getInstance().loadStorage(args[0]) != null) {
            proxiedPlayer.sendMessage(new ComponentBuilder(String.format("Clan %s already exists!", args[0])).color(ChatColor.RED).create());

            return;
        }

        try {
            MongoDBProvider.getInstance().saveOrCreateStorage(new PluginClanStorage(args[0], proxiedPlayer.getUniqueId().toString(), UUID.randomUUID().toString(), 0));
        } catch (IllegalAccessException e) {
            proxiedPlayer.sendMessage(new ComponentBuilder("An error occurred").color(ChatColor.RED).create());
        }
    }
}