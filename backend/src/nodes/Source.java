package nodes;

import pubsub.Broker;
import pubsub.Publisher;

import java.util.concurrent.BlockingQueue;

public abstract class Source<Out> extends Node<String> implements Publisher<Out>, Runnable {

    /**
     * Queue of outgoing items of type T.
     */
    private BlockingQueue<Out> queue;

    /**
     * Reference of Broker to signal when an item is produced.
     */
    private Broker<Out> broker;

    public String initialize(String id, BlockingQueue<Out> queue, Broker<Out> broker) {
        super.initialize(id);
        this.queue = queue;
        this.broker = broker;
        return this.getId();
    }

    /**
     * Private method used for publishing new messages
     *
     * @param message Message to publish
     * @throws InterruptedException Thrown when interrupted
     */
    void publishMessage(Out message) throws InterruptedException {
        this.queue.put(message);
        this.broker.notifyNewMessage(this.getId(), message.hashCode());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                this.publishMessage(this.produceMessage());
            }
        } catch (InterruptedException e) {
            System.out.println("Publisher " + this.getId() + " Thread interrupted");
        }
    }

}