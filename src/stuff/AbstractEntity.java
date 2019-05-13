package stuff;

import java.util.concurrent.BlockingQueue;

public abstract class AbstractEntity<T> implements Runnable {

    protected int id;
    protected BlockingQueue<T> queue;

    // Run time should be the responsibility of this Entity, not external objects
    // Moreover, this can ignore externally appointed run-times and crash earlier
    // External entities must not rely on good behaviour from Publishers/Subscribers...
    protected double runTime = 5000;

    void setQueue(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    void setId(int id) {
        this.id = id;
    }

    int getId() {
        return this.id;
    }
}
