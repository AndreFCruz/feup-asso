package pubsub;

import java.util.concurrent.BlockingQueue;

public abstract class Sink<In, Out> implements Subscriber<In, Out>, Runnable {
    private int id;
    private BlockingQueue<In> queue;

    public void initialize(int id, BlockingQueue<In> queue) {
        this.id = id;
        this.queue = queue;
    }

    public int getId() {
        return this.id;
    }

    /**
     * May block if Queue is empty
     *
     * @return The pulled message
     * @throws InterruptedException thrown when interrupted
     */
    In pullMessage() throws InterruptedException {
        return this.queue.take();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                In msg = this.pullMessage();
                this.handleMessage(msg);
            }
        } catch (InterruptedException e) {
            System.out.println("Subscriber " + this.id + " Thread interrupted");
        }
    }
}
