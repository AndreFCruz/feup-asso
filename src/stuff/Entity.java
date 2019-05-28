package stuff;

import java.util.concurrent.BlockingQueue;


interface Entity<T> extends Runnable {

    /**
     * Initialize this Entity with the appropriate references
     *
     * @param id     This entity's ID (from Broker's registry)
     * @param queue  This entity's message Queue
     * @param broker Reference to message Broker
     */
    void initializeEntity(int id, BlockingQueue<T> queue, Broker<T> broker);

    /**
     * Getter for this entity's ID (from Broker's registry)
     *
     * @return This Entity's ID
     */
    int getId();

    /**
     * Getter for this entity's message Queue
     *
     * @return This Entity's Queue
     */
    BlockingQueue<T> getQueue();

    /**
     * Getter for this entity's Broker
     *
     * @return This Entity's Broker
     */
    Broker<T> getBroker();

}