package stuff;

import java.util.concurrent.BlockingQueue;

public abstract class AbstractEntity<T> implements Runnable, Entity<T> {

    private int id;
    private BlockingQueue<T> queue;
    private Broker<T> broker;

    public void initializeEntity(int id, BlockingQueue<T> queue, Broker<T> broker) {
        this.id = id;
        this.queue = queue;
        this.broker = broker;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public BlockingQueue<T> getQueue() {
        return this.queue;
    }

    @Override
    public Broker<T> getBroker() {
        return this.broker;
    }


}
