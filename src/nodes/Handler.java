package nodes;

import manager.Broker;

import java.util.concurrent.BlockingQueue;

public abstract class Handler<In, Out> implements Runnable {

    private int outputId;
    private int inputId;
    private BlockingQueue<Out> publishQueue;
    private BlockingQueue<In> subscribeQueue;
    private Broker<Object> broker;

    public void initializeEntity(int outputId, int inputId, BlockingQueue<Out> publishQueue, BlockingQueue<In> subscribeQueue, Broker<Object> broker) {
        this.outputId = outputId;
        this.inputId = inputId;
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
    protected abstract Out handleMessage(In message) throws InterruptedException;

    /**
     * May block if Queue is empty
     *
     * @return The pulled message
     * @throws InterruptedException thrown when interrupted
     */
    private In pullMessage() throws InterruptedException {
        return this.subscribeQueue.take();
    }

    /**
     * Private method used for publishing new messages
     *
     * @param message Message to publish
     * @throws InterruptedException Thrown when interrupted
     */
    private void publishMessage(Out message) throws InterruptedException {
        this.publishQueue.put(message);
        this.broker.notifyNewMessage(this.outputId, message.hashCode());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                In message = this.pullMessage();
                Out processedMessage = this.handleMessage(message);
                this.publishMessage(processedMessage);
            }
        } catch (InterruptedException e) {
            System.out.println("Handler " + this.inputId + "|" + this.outputId + " Thread interrupted");
        }
    }
}