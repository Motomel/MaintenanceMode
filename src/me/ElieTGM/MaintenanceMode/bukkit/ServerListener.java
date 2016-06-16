
package me.ElieTGM.MaintenanceMode.bukkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.ElieTGM.MaintenanceMode.bukkit.event.WhitelistUpdateEvent;
import net.md_5.bungee.api.Favicon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;


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
    public void join(PlayerLoginEvent event) {
        if(parent.getEnabled()) {
            String name = event.getPlayer().getName();
            if(!event.getPlayer().hasPermission("maintenance.bypass")) {
                if (!parent.getWhitelist().contains(name)) {
                    event.setKickMessage(parent.getKickMessage().replaceAll("%newline", "\n"));
                    event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
                    
                    
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void ping(ServerListPingEvent e) {
    	
    	if(BukkitPlugin.getInstance().getConfig().getBoolean("motdbeforemaintenance", true)) {
    		
    		e.setMotd((BukkitPlugin.getInstance().getConfig().getString("messages.beforemaintenancemotd")).replaceAll("%newline", "\n").replace('&', '§'));
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
