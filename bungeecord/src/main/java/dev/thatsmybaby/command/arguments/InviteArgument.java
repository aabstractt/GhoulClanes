package dev.thatsmybaby.command.arguments;

import dev.thatsmybaby.BungeeClansLoader;
import dev.thatsmybaby.command.Argument;
import dev.thatsmybaby.shared.MongoDBProvider;
import dev.thatsmybaby.shared.storage.PluginClanStorage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public final class InviteArgument extends Argument {

    public InviteArgument(String name, String permission, String[] aliases, boolean async) {
        super(name, permission, aliases, async);
    }

    @Override
    public void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args) throws IllegalAccessException {
        MongoDBProvider provider = MongoDBProvider.getInstance();

        if (args.length == 0) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Usage: /clan invite <player>").color(ChatColor.RED).create());

            return;
        }

        PluginClanStorage clanStorage = provider.getPlayerClan(proxiedPlayer.getUniqueId());

        if (clanStorage == null) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Necesitas estar en un clan").create());

            return;
        }

        UUID targetUniqueId = provider.getTargetPlayer(args[0]);

        if (targetUniqueId == null) {
            proxiedPlayer.sendMessage(new ComponentBuilder(args[0] + " not found").create());

            return;
        }

        if (BungeeClansLoader.released() && targetUniqueId.equals(proxiedPlayer.getUniqueId())) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Error...").create());
        }

        if (provider.getPlayerClan(targetUniqueId) != null) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Player already in clan").create());

            return;
        }

        List<String> pendingInvites = provider.getTargetPendingInvites(targetUniqueId);

        if (pendingInvites.contains(clanStorage.getUniqueId())) {
            proxiedPlayer.sendMessage(new ComponentBuilder("Player " + args[0] + " already invited").create());

            return;
        }

        pendingInvites.add(clanStorage.getUniqueId());
        provider.createOrSave(targetUniqueId, pendingInvites);

        clanStorage.getPendingInvitesSent().add(targetUniqueId.toString());
        provider.createOrSaveStorage(clanStorage);

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetUniqueId);

        if (target == null) {
            return;
        }

        target.sendMessage(new ComponentBuilder("Clan invite received from " + proxiedPlayer.getName() + " (" + clanStorage.getName() + ")").create());
    }
}