package nodes;

import pubsub.Subscriber;

import java.util.concurrent.BlockingQueue;

public abstract class Sink<In, Out> extends Node implements Subscriber<In, Out>, Runnable {

    /**
     * Helper class definition for Sinks with no return type.
     * @param <T> The input data type.
     */
    public static abstract class EndSink<T> extends Sink<T, Void> {}

    private BlockingQueue<In> queue;

    public void initialize(int id, BlockingQueue<In> queue) {
        super.initialize(id);
        this.queue = queue;
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
                this.handleMessage(this.pullMessage());
            }
        } catch (InterruptedException e) {
            System.out.println("Subscriber " + this.getId() + " Thread interrupted");
        }
    }
}
