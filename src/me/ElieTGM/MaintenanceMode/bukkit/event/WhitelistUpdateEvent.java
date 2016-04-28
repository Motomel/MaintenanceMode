
package me.ElieTGM.MaintenanceMode.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class WhitelistUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Store the player name
     */
    private final String name;

    /**
     * Store the operation type
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
