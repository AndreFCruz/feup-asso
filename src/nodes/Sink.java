package nodes;

import java.util.concurrent.BlockingQueue;

public abstract class Sink<T> implements Runnable {

    protected int id;
    private BlockingQueue<T> queue;

    public void initializeEntity(int id, BlockingQueue<T> queue) {
        this.id = id;
        this.queue = queue;
    }

    /**
     * Handle the given Message
     * May block!
     *
     * @param message the message
     * @throws InterruptedException thrown when interrupted
     */
    protected abstract void handleMessage(T message) throws InterruptedException;

    /**
     * May block if Queue is empty
     *
     * @return The pulled message
     * @throws InterruptedException thrown when interrupted
     */
    private T pullMessage() throws InterruptedException {
        return this.queue.take();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                T msg = this.pullMessage();
                this.handleMessage(msg);
            }
        } catch (InterruptedException e) {
            System.out.println("Subscriber " + this.id + " Thread interrupted");
        }
    }
}