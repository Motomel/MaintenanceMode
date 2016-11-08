
package me.ElieTGM.MaintenanceMode.bukkit.command;

import com.google.common.base.Joiner;

import me.ElieTGM.MaintenanceMode.bukkit.BukkitPlugin;
import me.ElieTGM.MaintenanceMode.bukkit.EnableRunnable;
import me.ElieTGM.MaintenanceMode.bukkit.event.WhitelistUpdateEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static me.ElieTGM.MaintenanceMode.Messages.*;


public class CommandMaintenance implements CommandExecutor, TabExecutor {

    /**
     * Store the parent plugin
     */
    private final BukkitPlugin parent;

    /**
     * Create a new instance of the class
     * @param parent parent {@link me.ElieTGM.MaintenanceMode.bukkit.BukkitPlugin}
     */
    public CommandMaintenance(BukkitPlugin parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args != null && args.length > 0) {
            String sub = args[0];

            switch (sub.toLowerCase()) {
                case "enable":
                    if (parent.getEnabled()) {
                        sender.sendMessage(MAINTENANCE_ENABLED_ALREADY);
                        break;
                    }

                    if(parent.getCountdown() > 0) {
                        if (parent.getTaskId() != -1) {
                            sender.sendMessage(MAINTENANCE_TASK_ALREADY_RUNNING);
                            break;
                        }

                        EnableRunnable runnable = new EnableRunnable(parent, sender);
                        @SuppressWarnings("deprecation")
						BukkitTask task = Bukkit.getScheduler().runTask(parent, runnable);
                        parent.setTaskId(task.getTaskId());
                    } else {
                        parent.kick(null);
                        parent.setMaintenanceEnabled(true);
                        sender.sendMessage(MAINTENANCE_ENABLED);
                    }
                    break;
                case "cancel":
                    if (parent.getTaskId() == -1) {
                        sender.sendMessage(MAINTENANCE_TASK_NOT_RUNNING);
                        break;
                    }

                    parent.clearTask();
                    Bukkit.broadcastMessage(MAINTENANCE_TASK_STOPPED);
                    break;
                case "disable":
                    if (!parent.getEnabled()) {
                        sender.sendMessage(MAINTENANCE_DISABLED_ALREADY);
                        break;
                    }

                    parent.setMaintenanceEnabled(false);
                    sender.sendMessage(MAINTENANCE_DISABLED);
                    break;
                case "add":
                case "remove":
                    if (args.length > 1) {
                        String name = args[1];
                        switch (sub) {
                            case "add":
                                if (parent.getWhitelist().contains(name)) {
                                    sender.sendMessage(WHITELIST_ADD_EXIST);
                                } else {
                                    parent.getWhitelist().add(name);

                                    WhitelistUpdateEvent event = new WhitelistUpdateEvent(name, WhitelistUpdateEvent.Operation.ADD);
                                    Bukkit.getPluginManager().callEvent(event);

                                    sender.sendMessage(WHITELIST_ADD);
                                }
                                break;
                            case "remove":
                                if (!parent.getWhitelist().contains(name)) {
                                    sender.sendMessage(WHITELIST_DEL_EXIST);
                                } else {
                                    parent.getWhitelist().remove(name);

                                    WhitelistUpdateEvent event = new WhitelistUpdateEvent(name, WhitelistUpdateEvent.Operation.REMOVE);
                                    Bukkit.getPluginManager().callEvent(event);

                                    sender.sendMessage(WHITELIST_DEL);
                                }
                                break;
                            default:
                                sender.sendMessage(HELP_INVALID + "maintenance <add|remove> <username>");
                                break;
                        }
                        break;
                    } else {
                        sender.sendMessage(HELP_INVALID + "maintenance <add|remove> <username>");
                    }
                    break;
                case "list":
                    Joiner joiner = Joiner.on(", ");
                    String joined = joiner.join(parent.getWhitelist());
                    sender.sendMessage(ChatColor.GREEN + "Whitelist: " + ChatColor.WHITE + (joined.equals("") ? "No players added to whitelist" : joined));
                    break;
                case "reload":
                    parent.reload(true);
                    sender.sendMessage(CONFIG_RELOAD);
                    break;
                case "help":
                    sender.sendMessage(HELP_USAGE);
                    break;
                default:
                    sender.sendMessage(HELP_MESSAGE);
                    break;
            }
            return true;
        } else {
            sender.sendMessage(HELP_MESSAGE);
            return false;
        }
    }

    /**
     * Add tab completion to commands
     *
     * @param sender command sender
     * @param args arguments
     * @return list of potential tab results
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1 || args.length > 2) {
            return new ArrayList<>();
        }

        List<String> commands = new ArrayList<>();

        if(args.length == 1) {
            String search = args[0].toLowerCase();
            if("list".startsWith(search)) {
                commands.add("list");
            }

            if("add".startsWith(search)) {
                commands.add("add");
            }

            if("remove".startsWith(search)) {
                commands.add("remove");
            }

            if ("cancel".startsWith(search)) {
                commands.add("cancel");
            }

            if("help".startsWith(search)) {
                commands.add("help");
            }

            if("enable".startsWith(search)) {
                commands.add("enable");
            }

            if("disable".startsWith(search)) {
                commands.add("disable");
            }

            if("reload".startsWith(search)) {
                commands.add("reload");
            }
        } else {
            String sub = args[0].toLowerCase();
            if(sub.equals("add")) {
                String search = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(search)) {
                        if(!parent.getWhitelist().contains(player.getName())) {
                            commands.add(player.getName());
                        }
                    }
                }
            } else if (sub.equals("remove")) {
                String search = args[1].toLowerCase();
                for(String name : parent.getWhitelist()) {
                    if(name.toLowerCase().startsWith(search)) {
                        commands.add(name);
                    }
                }
            }
        }

        return commands;
    }

}
