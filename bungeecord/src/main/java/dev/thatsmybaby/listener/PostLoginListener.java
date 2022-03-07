package dev.thatsmybaby.listener;

import dev.thatsmybaby.BungeeClansLoader;
import dev.thatsmybaby.shared.MongoDBProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class PostLoginListener implements Listener {

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent ev) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeClansLoader.getInstance(), () -> this.handlePostLogin(ev.getPlayer()));
    }

    private void handlePostLogin(ProxiedPlayer proxiedPlayer) {
        MongoDBProvider.getInstance().createOrSave(proxiedPlayer.getName(), proxiedPlayer.getUniqueId());
    }
}