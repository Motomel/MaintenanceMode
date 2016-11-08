
package me.ElieTGM.MaintenanceMode.bukkit;

import me.ElieTGM.MaintenanceMode.Messages;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import static me.ElieTGM.MaintenanceMode.Messages.MAINTENANCE_ENABLED;

public class EnableRunnable extends BukkitRunnable {

    private final BukkitPlugin parent;
    private final CommandSender sender;

    public EnableRunnable(BukkitPlugin parent, CommandSender sender) {
        this.parent = parent;
        this.sender = sender;
    }

    @Override
    public void run() {
        int loops = parent.getCountdown();
        Bukkit.getServer().broadcastMessage(Messages.colour(format(parent.getCountdownMessage(), loops)));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = loops - 1; i > 0; i--) {
            if (parent.getAlertTimes().contains(i)) {
                Bukkit.getServer().broadcastMessage(Messages.colour(format(parent.getCountdownMessage(), i)));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i == 1) {
                parent.kick(null);
                parent.setMaintenanceEnabled(true);
                sender.sendMessage(MAINTENANCE_ENABLED);
            }
        }
        parent.setTaskId(-1);
    }

    private String format(String message, int seconds) {
        return message.replace("{{ TIME }}", DurationFormatUtils.formatDurationWords(seconds * 1000, true, false)).replace("{{ SECONDS }}", String.valueOf(seconds));
    }
}
