package me.ElieTGM.MaintenanceMode.bungee;

import java.lang.reflect.Proxy;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;

import me.ElieTGM.MaintenanceMode.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import static me.ElieTGM.MaintenanceMode.Messages.MAINTENANCE_ENABLED;

public class EnableRunnable implements Runnable {

    private final BungeePlugin parent;
    private final CommandSender sender;

    public EnableRunnable(BungeePlugin parent, CommandSender sender) {
        this.parent = parent;
        this.sender = sender;
    }

    @Override
    public void run() {
        int loops = parent.getCountdown();
        ProxyServer.getInstance().broadcast(Messages.colour(format(parent.getCountdownMessage(), loops)));
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        for (int i = loops - 1; i > 0; i--) {
            if (parent.getAlertTimes().contains(i)) {
                ProxyServer.getInstance().broadcast(Messages.colour(format(parent.getCountdownMessage(), i)));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i == 1) {
                parent.kick(null);
                parent.setEnabled(true);
                sender.sendMessage(MAINTENANCE_ENABLED);
            }
        }
        parent.setTaskId(-1);
    }

    private String format(String message, int seconds) {
        return message.replace("{{ TIME }}", DurationFormatUtils.formatDurationWords(seconds * 1000, true, false)).replace("{{ SECONDS }}", String.valueOf(seconds));
    }
}