package stuff;

import java.util.concurrent.BlockingQueue;

public abstract class AbstractEntity<T> implements Runnable {

    // Run time should be the responsibility of this Entity, not external objects
    // Moreover, this can ignore externally appointed run-times and crash earlier
    // External entities must not rely on good behaviour from Publishers/Subscribers...
    protected double runTime = 5000; // TODO delete runtime manhoso

    protected int id;
    protected BlockingQueue<T> queue;
    Broker<T> broker;

    void initializeEntity(int id, BlockingQueue<T> queue, Broker<T> broker) {
        this.id = id;
        this.queue = queue;
        this.broker = broker;
    }

    int getId() {
        return this.id;
    }
}
