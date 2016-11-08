package me.ElieTGM.MaintenanceMode;


public class Messages {

    /**
     * Message that's displayed when the command is run without arguments or an invalid subcommand
     */
    public static final String HELP_MESSAGE = ChatColor.YELLOW + "Welcome to MaintenanceMode, maintained by ElieTGM. Use /mm help to check a bunch of commands.";

    /**
     * Message that's displayed when the "help" subcommand is executed
     */
    public static final String HELP_USAGE = ChatColor.YELLOW + "Usage: /maintenance <enable|disable|cancel|list|add|remove|reload|help>";

    /**
     * Message that's displayed when the "reload" subcommand is executed and succeeds
     */
    public static final String CONFIG_RELOAD = ChatColor.GREEN + "Configuration has been reloaded :)";

    /**
     * Message that's displayed then the "reload" subcommand is executed and fails
     */
    public static final String CONFIG_RELOAD_ERR = ChatColor.RED + "Unable to reload configuration :(";

    /**
     * Message that's displayed when the invalid arguments are passed to a subcommand
     */
    public static final String HELP_INVALID = ChatColor.RED + "Invalid arguments provided, try this: /";

    /**
     * Message that's displayed when the "enable" subcommand is executed
     */
    public static final String MAINTENANCE_ENABLED = ChatColor.GREEN + "Maintenance mode has been enabled!";

    /**
     * Message that's displayed when the "enable" subcommand is executed but maintenance mode is already active
     */
    public static final String MAINTENANCE_ENABLED_ALREADY = ChatColor.RED + "Maintenance mode is already enabled!";

    /**
     * Message that's displayed when the "disable" subcommand is executed
     */
    public static final String MAINTENANCE_DISABLED = ChatColor.GREEN + "Maintenance mode has been disabled!";

    /**
     * Message that's displayed when the "disable" subcommand is executed but maintenance mode is already disabled
     */
    public static final String MAINTENANCE_DISABLED_ALREADY = ChatColor.RED + "Maintenance mode isn't enabled!";

    public static final String MAINTENANCE_TASK_ALREADY_RUNNING = ChatColor.RED + "Maintenance mode is already scheduled to turn on!";

    /**
     * Message that's displayed when the "cancel" subcommand is executed but no runnable is active
     */
    public static final String MAINTENANCE_TASK_NOT_RUNNING = ChatColor.RED + "Maintenance mode is not scheduled to be enabled!";

    /**
     * Message that's displayted when the "cancel" subcommand is executed and is successfully executed.
     */
    public static final String MAINTENANCE_TASK_STOPPED = ChatColor.GREEN + "Scheduled maintenance has been cancelled!";

    /**
     * Message that's displayed when the "add" subcommand is executed and succeeds
     */
    public static final String WHITELIST_ADD = ChatColor.GREEN + "Player added to the whitelist!";

    /**
     * Message that's displayed when the "add" subcommand is executed and fails
     */
    public static final String WHITELIST_ADD_EXIST = ChatColor.RED + "That player is already whitelisted!";

    /**
     * Message that's displayed when the "remove" subcommand is executed and succeeds
     */
    public static final String WHITELIST_DEL = ChatColor.GREEN + "Player removed from the whitelist!";

    /**
     * Message that's displayed when the "remove" subcommand is executed and fails
     */
    public static final String WHITELIST_DEL_EXIST = ChatColor.RED + "That player is not whitelisted!";

    /**
     * Stop people creating an instance of the class
     */
    private Messages() {

    }

    /**
     * I really hate typing out the code to change colours every time.
     * @param in input string
     * @return Bukkit/Bungee formatted String
     */
    public static String colour(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }
}
