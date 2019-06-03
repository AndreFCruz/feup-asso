package nodes;

import manager.Broker;
import pubsub.Publisher;

import java.util.concurrent.BlockingQueue;

public abstract class Source<Out> implements Publisher<Out>, Runnable {

    /**
     * This entity's unique ID.
     */
    private int id;

    /**
     * Queue of outgoing items of type T.
     */
    private BlockingQueue<Out> queue;

    /**
     * Reference of Broker to signal when an item is produced.
     */
    private Broker<Out> broker;

    public void initialize(int id, BlockingQueue<Out> queue, Broker<Out> broker) {
        this.id = id;
        this.queue = queue;
        this.broker = broker;
    }

    public int getId() {
        return this.id;
    }

    /**
     * Private method used for publishing new messages
     *
     * @param message Message to publish
     * @throws InterruptedException Thrown when interrupted
     */
    void publishMessage(Out message) throws InterruptedException {
        this.queue.put(message);
        this.broker.notifyNewMessage(this.id, message.hashCode());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                this.publishMessage(this.produceMessage());
            }
        } catch (InterruptedException e) {
            System.out.println("Publisher " + this.id + " Thread interrupted");
        }
    }

}