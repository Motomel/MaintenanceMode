

package me.ElieTGM.MaintenanceMode.bungee;

import me.ElieTGM.MaintenanceMode.bungee.command.CommandMaintenance;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static me.ElieTGM.MaintenanceMode.Messages.colour;

public class BungeePlugin extends Plugin implements Listener {

    /**
     * Explicitly state we want to look for config.yml
     */
    private final String file = "config.yml";

    /**
     * Store the response that is sent to clients when the server is pinged in maintenance mode
     */
    private ServerPing.Protocol protocol;

    /**
     * Store the list of whitelisted players
     */
    private List<String> whitelist;

    /**
     * Store the Configuration object
     */
    private Configuration config;

    /**
     * Store the message players are shown when kicked
     */
    private String message_kick;

    /**
     * Store when to display an alert
     */
    private List<Integer> alert;

    /**
     * Store the countdown message
     */
    private String countdownMessage;

    /**
     * Store the number of seconds the countdown will take
     */
    private int countdown;

    /**
     * Store whether maintenance mode is enabled or not
     */
    private boolean enabled;

    /**
     * MOTD to display when the server is pinged
     */
    private String motd;

    /**
     * Active task ID
     */
    private int taskId = -1;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            whitelist = new ArrayList<>();
        } catch (IOException e) {
            getLogger().severe("Unable to save default config file");
        } finally {
            if(reload()) {
                getLogger().info("Successfully loaded configuration values");
            } else {
                getLogger().severe("Unable to load configuration values");
            }
        }

        this.protocol = new ServerPing.Protocol("Maintenance", Short.MAX_VALUE);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMaintenance(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerListener(this));
    }

    @Override
    public void onDisable() {
        save();
    }

    /**
     * Reload the configuration object
     * @return true if the config loaded successfully
     */
    public boolean reload() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), this.file));
            getLogger().info("Loaded configuration from file");
        } catch (IOException e) {
            getLogger().severe("Unable to load configuration file");
            return false;
        }

        this.countdown = getConfig().getInt("activation.countdown", 15);
        this.alert = getConfig().getIntList("activation.announce");
        this.countdownMessage = getConfig().getString("messages.activation", "&cServer entering maintenance mode in {{ TIME }}");
        this.enabled = getConfig().getBoolean("enabled", false);
        this.whitelist = getConfig().getStringList("whitelist");
        this.message_kick = colour(getConfig().getString("messages.kick", "&cThe server is in maintenance mode, sorry for any inconvenience."));
        this.motd = colour(getConfig().getString("messages.motd", "&c&lMaintenance mode enabled!"));
        return true;
    }

    /**
     * Kick a {@link net.md_5.bungee.api.connection.ProxiedPlayer} from the server
     * @param kick null to kick all players
     */
    @SuppressWarnings("deprecation")
	public void kick(ProxiedPlayer kick) {
        if(kick == null) {
            boolean skip = (whitelist == null || whitelist.size() < 1);

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if(skip) {
                    player.disconnect(this.message_kick);
                } else {
                    if(!player.hasPermission("maintenance.bypass")) {
                        if (!whitelist.contains(player.getName())) {
                            player.disconnect(this.message_kick);
                        }
                    }
                }
            }
        } else {
            kick.disconnect(this.message_kick);
        }
    }

    /**
     * Save the configuration values to the configuration file
     */
    private void save() {
        try {
            config.set("enabled", this.enabled);
            config.set("whitelist", this.whitelist);

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), this.file));
            getLogger().log(Level.INFO, "Saved configuration file");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Unable to save configuration file");
        }
    }

    /**
     * Implementation of Bukkit's saveDefaultConfig()
     * @throws java.io.IOException
     */
    private void saveDefaultConfig() throws IOException {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            Files.copy(getResourceAsStream("config.yml"), file.toPath());
        }
    }

    /**
     * Get the configuration object
     * @return {@link net.md_5.bungee.config.Configuration} containing configuration values
     */
    public Configuration getConfig() {
        return this.config;
    }

    /**
     * Get whether maintenance mode is active
     * @return true if enabled
     */
    public boolean getEnabled() {
        return this.enabled;
    }

    /**
     * Set whether maintenance mode is enabled
     * @param enabled true to enable maintenance mode
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the list of whitelisted players
     * @return {@link java.util.List} containing player names
     */
    public List<String> getWhitelist() {
        return this.whitelist;
    }

    /**
     * Get the protocol object
     * @return {@link net.md_5.bungee.api.ServerPing} containing maintenance mode data
     */
    public ServerPing.Protocol getProtocol() {
        return this.protocol;
    }

    /**
     * Get the list of times to alert on
     * @return {@link java.util.List} of integers
     */
    public List<Integer> getAlertTimes() {
        return this.alert;
    }

    /**
     * Get the kick message
     * @return message to display to kicked players
     */
    public String getKickMessage() {
        return message_kick;
    }

    /**
     * Get the "message of the day"
     * @return message to display in server list
     */
    public String getMOTD() {
        return motd;
    }

    /**
     * Get the number of seconds counted down before maintenance mode is activated
     * @return integer
     */
    public int getCountdown() {
        return this.countdown;
    }

    /**
     * Get the message format for countdown broadcasts
     * @return countdown message format
     */
    public String getCountdownMessage() {
        return this.countdownMessage;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int id) {
        this.taskId = id;
    }

    public void clearTask() {
        if (taskId != -1) {
            ProxyServer.getInstance().getScheduler().cancel(taskId);
        }
        taskId = -1;
    }
}
