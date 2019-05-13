package stuff;

import java.util.concurrent.BlockingQueue;

public abstract class AbstractEntity<T> implements Runnable {

    protected BlockingQueue<T> queue;

    // Run time should be the responsibility of this Entity, not external objects
    // Moreover, this can ignore externally apointed run-times and crash earlier
    // External entities must not rely on good behaviour from Publishers/Subcribers...
    protected double runTime = 5000;

    void initVariables(BlockingQueue<T> queue) {
        this.queue = queue;
    }
}
