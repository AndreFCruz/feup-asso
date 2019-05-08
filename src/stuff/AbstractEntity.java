package stuff;

import java.util.concurrent.BlockingQueue;

public class AbstractEntity<T> {

    protected BlockingQueue<T> queue;
    protected double runTime;

    void initVariables(BlockingQueue<T> queue, double runTime){
        this.queue = queue;
        this.runTime = runTime;
    }
}
