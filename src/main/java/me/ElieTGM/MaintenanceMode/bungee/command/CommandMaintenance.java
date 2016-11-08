
package me.ElieTGM.MaintenanceMode.bungee.command;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import me.ElieTGM.MaintenanceMode.bungee.BungeePlugin;
import me.ElieTGM.MaintenanceMode.bungee.EnableRunnable;
import me.ElieTGM.MaintenanceMode.bungee.event.WhitelistUpdateEvent;
import me.ElieTGM.MaintenanceMode.bungee.event.WhitelistUpdateEvent.Operation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.List;

import static me.ElieTGM.MaintenanceMode.Messages.*;


@SuppressWarnings("deprecation")
public class CommandMaintenance extends Command implements TabExecutor {

    /**
     * Store the parent plugin
     */
    private final BungeePlugin parent;

    /**
     * Create a new instance of the class
     * @param parent parent {@link me.ElieTGM.MaintenanceMode.bungee.BungeePlugin}
     */
    public CommandMaintenance(BungeePlugin parent) {
        super("maintenance", "maintenance.toggle", "mm", "mmw");
        this.parent = parent;
    }

    /**
     * Handle the various subcommands associated with the plugin
     *
     * @param sender command sender
     * @param args command arguments
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
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
                        ScheduledTask task = ProxyServer.getInstance().getScheduler().runAsync(parent, runnable);
                        parent.setTaskId(task.getId());
                    } else {
                        parent.kick(null);
                        parent.setEnabled(true);
                        sender.sendMessage(MAINTENANCE_ENABLED);
                    }
                    break;
                case "cancel":
                    if (parent.getTaskId() != -1) {
                        sender.sendMessage(MAINTENANCE_TASK_NOT_RUNNING);
                        break;
                    } else {

                    parent.clearTask();
                    ProxyServer.getInstance().broadcast(MAINTENANCE_TASK_STOPPED);
                    break;
                    
                    }
                case "disable":
                    if (!parent.getEnabled()) {
                        sender.sendMessage(MAINTENANCE_DISABLED_ALREADY);
                        break;
                    }

                    parent.setEnabled(false);
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

                                    WhitelistUpdateEvent event = new WhitelistUpdateEvent(name, Operation.ADD);
                                    ProxyServer.getInstance().getPluginManager().callEvent(event);

                                    sender.sendMessage(WHITELIST_ADD);
                                }
                                break;
                            case "remove":
                                if (!parent.getWhitelist().contains(name)) {
                                    sender.sendMessage(WHITELIST_DEL_EXIST);
                                } else {
                                    parent.getWhitelist().remove(name);

                                    WhitelistUpdateEvent event = new WhitelistUpdateEvent(name, Operation.REMOVE);
                                    ProxyServer.getInstance().getPluginManager().callEvent(event);

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
                    if (parent.reload()) {
                        sender.sendMessage(CONFIG_RELOAD);
                    } else {
                        sender.sendMessage(CONFIG_RELOAD_ERR);
                    }
                    break;
                case "help":
                    sender.sendMessage(HELP_USAGE);
                    break;
                default:
                    sender.sendMessage(HELP_MESSAGE);
                    break;
                    
                	
            }
        } else {
            sender.sendMessage(HELP_MESSAGE);
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
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length < 1 || args.length > 2) {
            return ImmutableSet.of();
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
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
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
