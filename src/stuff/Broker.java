package stuff;

import utils.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Broker<T> implements Runnable {

    // Registry key(Publisher | Subscriber) -> queue
    private Registry<BlockingQueue<T>> registry = new Registry<BlockingQueue<T>>();
    // publisherKey -> arraySubscriberKeys
    private Map<Integer, ArrayList<Integer>> observers = new HashMap<>();

    private final double runTime;
    private ScheduledExecutorService executor;

    public Broker(double runTime) {
        this.runTime = runTime;
        this.executor = Executors.newScheduledThreadPool(10);
    }

    public int addPublisher(Publisher<T> obj, boolean run){
        return this.registAndRun(obj, run);
    }

    public void addSubscriber(Subscriber<T> obj, int publisherId, boolean run){
        int key = this.registAndRun(obj, run);
        ArrayList<Integer> subscribersList = new ArrayList<>();
        if(this.observers.containsKey(publisherId)){
            subscribersList = this.observers.get(publisherId);
        }
        subscribersList.add(key);
        this.observers.put(publisherId, subscribersList);
    }

    private int registAndRun(AbstractEntity<T> obj, boolean run) {
        BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
        Object key = registry.register(queue);
        obj.initVariables(queue, this.runTime);
        if(run)
            executor.submit((Runnable) obj);
        return (int) key;
    }

    void movesMessages() throws InterruptedException {
        for (Map.Entry<Integer, ArrayList<Integer>> entry : this.observers.entrySet()) {
            int publisherKey = entry.getKey();
            BlockingQueue<T> publisherQueue = this.registry.get(publisherKey);
            if(publisherQueue.peek() == null) {
                continue;
            }

            T message = publisherQueue.take();

            ArrayList<Integer> subscribers = this.observers.get(publisherKey);
            for(int subscriber : subscribers){
                BlockingQueue<T> subscriberQueue = this.registry.get(subscriber);
                System.out.println(message);
                subscriberQueue.add(message);
            }
        }
    }

    @Override
    public void run() {
        double start = System.currentTimeMillis();
        while(start + runTime > System.currentTimeMillis()){
            try {
                movesMessages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
