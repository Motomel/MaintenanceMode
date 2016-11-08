package me.ElieTGM.MaintenanceMode.bungee;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;




import me.ElieTGM.MaintenanceMode.bungee.event.WhitelistUpdateEvent;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;


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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ping(ProxyPingEvent event) {
        if (parent.getEnabled()) {
        	
        	if(parent.getConfig().getBoolean("VersionProtocol", true)) {
        		
            event.getResponse().setVersion(parent.getProtocol());
            
        	}
        	
        	if(parent.getConfig().getBoolean("ChangeServerIconInMaintenance", true)) {
        		
        	String faviconpath = parent.getDataFolder().toString() + "/server-icon.png";
        	
        	BufferedImage img = null;
        	
        	try {
        		
        		
				img = ImageIO.read(new File(faviconpath));
				
				event.getResponse().setFavicon(Favicon.create(img));
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
        	
        	}
        	
        	
        	
            event.getResponse().setDescription(parent.getMOTD().replaceAll("%newline", "\n"));

          
        } else {
        	if(parent.getConfig().getBoolean("motdbeforemaintenance", true)) {
        		
        		event.getResponse().setDescription(parent.getBeforeMOTD().replaceAll("%newline", "\n"));
        	}
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(PreLoginEvent event) {
        if(parent.getEnabled()) {
            String name = event.getConnection().getName();
            if(!parent.getWhitelist().contains(name)) {
                event.setCancelled(true);
                event.setCancelReason(parent.getKickMessage().replaceAll("%newline", "\n"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
