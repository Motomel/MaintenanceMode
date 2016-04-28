package me.ElieTGM.MaintenanceMode.bungee;

import me.ElieTGM.MaintenanceMode.bungee.event.WhitelistUpdateEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class ServerListener implements Listener {

    /**
     * Store the parent plugin
     */
    private final BungeePlugin parent;

    /**
     * Create a new instance of the class
     *
     * @param parent parent plugin
     */
    public ServerListener(BungeePlugin parent) {
        this.parent = parent;
    }

    @EventHandler
    public void ping(ProxyPingEvent event) {
        if (parent.getEnabled()) {
            event.getResponse().setVersion(parent.getProtocol());
            event.getResponse().setDescription(parent.getMOTD());
        }
    }

    @EventHandler
    public void join(PreLoginEvent event) {
        if(parent.getEnabled()) {
            String name = event.getConnection().getName();
            if(!parent.getWhitelist().contains(name)) {
                event.setCancelled(true);
                event.setCancelReason(parent.getKickMessage());
            }
        }
    }

    @EventHandler
    public void update(WhitelistUpdateEvent event) {
        if(parent.getEnabled()) {
            if (event.getOperation() == WhitelistUpdateEvent.Operation.REMOVE) {
                ProxiedPlayer player;

                if ((player = ProxyServer.getInstance().getPlayer(event.getName())) != null) {
                    if(!player.hasPermission("maintenance.bypass")) {
                        parent.kick(player);
                    }
                }
            }
        }
    }

}
