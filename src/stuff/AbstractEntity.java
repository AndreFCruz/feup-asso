package stuff;

import java.util.concurrent.BlockingQueue;

public class AbstractEntity{

    protected BlockingQueue queue;
    protected double runTime;

    void initVariables(BlockingQueue queue, double runTime){
        this.queue = queue;
        this.runTime = runTime;
    }
}
