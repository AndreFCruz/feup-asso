package nodes;

import manager.Broker;

import java.util.concurrent.BlockingQueue;

public abstract class Handler<T> implements Runnable {

    private int inputId;
    private int outputId;
    private BlockingQueue<T> publishQueue;
    private BlockingQueue<T> subscribeQueue;
    private Broker<T> broker;

    public void initializeEntity(int inputId, int outputId, BlockingQueue<T> publishQueue, BlockingQueue<T> subscribeQueue, Broker<T> broker) {
        this.inputId = inputId;
        this.outputId = outputId;
        this.publishQueue = publishQueue;
        this.subscribeQueue = subscribeQueue;
        this.broker = broker;
    }

    /**
     * Handle the given Message
     * May block!
     *
     * @param message the message
     * @return The processed message
     * @throws InterruptedException thrown when interrupted
     */
    protected abstract T handleMessage(T message) throws InterruptedException;

    /**
     * May block if Queue is empty
     *
     * @return The pulled message
     * @throws InterruptedException thrown when interrupted
     */
    private T pullMessage() throws InterruptedException {
        return this.subscribeQueue.take();
    }

    /**
     * Private method used for publishing new messages
     *
     * @param message Message to publish
     * @throws InterruptedException Thrown when interrupted
     */
    private void publishMessage(T message) throws InterruptedException {
        this.publishQueue.put(message);
        this.broker.notifyNewMessage(this.outputId, message.hashCode());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                T message = this.pullMessage();
                T processedMessage = this.handleMessage(message);
                this.publishMessage(processedMessage);
            }
        } catch (InterruptedException e) {
            System.out.println("Handler " + this.inputId + "|" + this.outputId + " Thread interrupted");
        }
    }
}