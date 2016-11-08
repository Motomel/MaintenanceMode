package me.ElieTGM.MaintenanceMode.bukkit.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Status.Server;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketAdapter.AdapterParameteters;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import me.ElieTGM.MaintenanceMode.bukkit.BukkitPlugin;

public class PingProtocolEvent {
	
  
  public static void addPingResponsePacketListener()
  {
    try
    {
      BukkitPlugin.getInstance().protocolManager.addPacketListener(new PacketAdapter(PacketAdapter.params(BukkitPlugin.getInstance(), new PacketType[] { PacketType.Status.Server.OUT_SERVER_INFO }).serverSide().gamePhase(GamePhase.BOTH).listenerPriority(ListenerPriority.HIGHEST).optionAsync())
      {
        public void onPacketSending(PacketEvent event)
        {
          try
          {
        	  if(BukkitPlugin.getInstance().getEnabled()) {
        		  
            WrappedServerPing ping = (WrappedServerPing)event.getPacket().getServerPings().getValues().get(0);
            
            if(BukkitPlugin.getInstance().getConfig().getBoolean("VersionProtocol", true)) {
            String pingMessage = BukkitPlugin.getInstance().getProtocolMessage();
            
            ping.setVersionProtocol(-1);
            ping.setVersionName(pingMessage);
            
            } else {
            	ping.setPlayersMaximum(0);
            }
            
            String motd = BukkitPlugin.getInstance().getMotd().replaceAll("%newline", "\n");
            
            ping.setMotD(motd);
            
            if(BukkitPlugin.getInstance().getConfig().getBoolean("ChangeServerIconInMaintenance", true)) {
            String iconpath = BukkitPlugin.getInstance().getDataFolder().toString() + "/server-icon.png";
            
            File iconfile = new File(iconpath);
            if (iconfile.exists())
            {
            	
              WrappedServerPing.CompressedImage favicon = WrappedServerPing.CompressedImage.fromPng(new FileInputStream(iconfile));
              ping.setFavicon(favicon);
              
            }
            }
            
            event.getPacket().getServerPings().getValues().set(0, ping);
            
        	  }
            
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
