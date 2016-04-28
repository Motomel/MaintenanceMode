
package me.ElieTGM.MaintenanceMode.bukkit;

import me.ElieTGM.MaintenanceMode.bukkit.event.WhitelistUpdateEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;


public class ServerListener implements Listener {

    /**
     * Store the parent plugin
     */
    private final BukkitPlugin parent;

    /**
     * Create a new instance of the class
     *
     * @param parent parent plugin
     */
    public ServerListener(BukkitPlugin parent) {
        this.parent = parent;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ping(ServerListPingEvent event) {
        if(parent.getEnabled()) {
            event.setMaxPlayers(0);
            event.setMotd(parent.getMotd());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerLoginEvent event) {
        if(parent.getEnabled()) {
            String name = event.getPlayer().getName();
            if(!event.getPlayer().hasPermission("maintenance.bypass")) {
                if (!parent.getWhitelist().contains(name)) {
                    event.setKickMessage(parent.getKickMessage());
                    event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
                }
            }
        }
    }

    @EventHandler
    public void update(WhitelistUpdateEvent event) {
        if(parent.getEnabled()) {
            if (event.getOperation() == WhitelistUpdateEvent.Operation.REMOVE) {
                Player player;

                if ((player = Bukkit.getPlayerExact(event.getName())) != null) {
                    if(!player.hasPermission("maintenance.bypass")) {
                        parent.kick(player);
                    }
                }
            }
        }
    }
}
