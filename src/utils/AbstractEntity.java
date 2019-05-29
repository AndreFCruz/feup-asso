package utils;

import manager.Broker;

public abstract class AbstractEntity<T> implements Runnable {

    private int id;
    private Broker<T> broker;

    /**
     * Initialize this Entity with the appropriate references
     *
     * @param id     This entity's ID (from Broker's registry)
     * @param broker Reference to message Broker
     */
    public AbstractEntity(int id, Broker<T> broker) {
        this.id = id;
        this.broker = broker;
    }

    /**
     * Getter for this entity's ID (from Broker's registry)
     *
     * @return This Entity's ID
     */
    protected int getId() {
        return this.id;
    }

    /**
     * Getter for this entity's Broker
     *
     * @return This Entity's Broker
     */
    protected Broker<T> getBroker() {
        return this.broker;
    }
}
