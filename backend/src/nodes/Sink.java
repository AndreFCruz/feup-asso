package nodes;

import pubsub.Subscriber;

import java.util.concurrent.BlockingQueue;

public abstract class Sink<In, Out> extends Node implements Subscriber<In, Out>, Runnable {

    /**
     * This node's unique ID.
     */
    private int id;
    private BlockingQueue<In> queue;

    public String initialize(int id, BlockingQueue<In> queue) {
        String nodeName = super.initialize(Integer.toString(id));
        this.id = id;
        this.queue = queue;
        return nodeName;
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

    public int getId() {
        return this.id;
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

    /**
     * Helper class definition for Sinks with no return type.
     *
     * @param <T> The input data type.
     */
    public static abstract class EndSink<T> extends Sink<T, Void> {
    }
}
