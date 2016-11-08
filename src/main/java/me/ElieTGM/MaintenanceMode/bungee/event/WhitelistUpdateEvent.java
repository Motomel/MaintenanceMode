
package me.ElieTGM.MaintenanceMode.bungee.event;

import net.md_5.bungee.api.plugin.Event;

public class WhitelistUpdateEvent extends Event {

    /**
     * Store the username
     */
    private final String name;

    /**
     * Store the {@link me.ElieTGM.MaintenanceMode.bungee.event.WhitelistUpdateEvent.Operation}
     */
    private final Operation operation;

    /**
     * Create a new instance of the class
     *
     * @param name player name
     * @param operation operation type
     */
    public WhitelistUpdateEvent(String name, Operation operation) {
        this.name = name;
        this.operation = operation;
    }

    /**
     * Get the value of {@link #operation}
     *
     * @return {@link #operation}
     */
    public Operation getOperation() {
        return this.operation;
    }

    /**
     * Get the value of {@link #name}
     *
     * @return {@link #name}
     */
    public String getName() {
         return this.name;
    }

    /**
     * Let us know whether people are being added to or removed from the whitelist
     */
    public enum Operation {
        ADD,
        REMOVE
    }
}
