package nodes;

import manager.Broker;

import java.util.concurrent.BlockingQueue;

public abstract class Source<T> implements Runnable {

    private int id;
    private BlockingQueue<T> queue;
    private Broker<T> broker;

    public void initializeEntity(int id, BlockingQueue<T> queue, Broker<T> broker) {
        this.id = id;
        this.queue = queue;
        this.broker = broker;
    }

    /**
     * Method for this Source to generate a Message.
     * May block!
     *
     * @return The generated Message
     * @throws InterruptedException Thrown when interrupted
     */
    protected abstract T produceMessage() throws InterruptedException;

    /**
     * Private method used for publishing new messages
     *
     * @param message Message to publish
     * @throws InterruptedException Thrown when interrupted
     */
    private void publishMessage(T message) throws InterruptedException {
        this.queue.put(message);
        this.broker.notifyNewMessage(this.id, message.hashCode());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                T message = this.produceMessage();
                this.publishMessage(message);
            }
        } catch (InterruptedException e) {
            System.out.println("Publisher " + this.id + " Thread interrupted");
        }
    }


}