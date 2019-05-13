package stuff;

import utils.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @T Message type
 */
public class Broker<T> implements Runnable {

    // Registry key(Publisher | Subscriber) -> queue
    private Registry<BlockingQueue<T>> registry = new Registry<BlockingQueue<T>>();

    // publisherKey -> arraySubscriberKeys
    private Map<Integer, ArrayList<Integer>> observers = new HashMap<>();

    public Broker() {
    }

    /**
     * Register a new Entity (Publisher | Subscriber)
     */
    public int register(AbstractEntity<T> entity) {
        BlockingQueue<T> queue = new LinkedBlockingQueue<>(); // TODO eventually limit queue size
        entity.setQueue(queue);
        int entityId = this.registry.register(queue);
        entity.setId(entityId);
        return entityId;
    }

    public void addSubscriber(int subscriberId, int publisherId) {
        ArrayList<Integer> subscribersList = this.observers.getOrDefault(publisherId, new ArrayList<>());
        subscribersList.add(subscriberId);
        this.observers.put(publisherId, subscribersList);
    }

    // TODO
    // Publisher should have reference to Broker in which it is registered
    // And publish message directly to Broker (so as to not poll registered queues,
    // going for event-based instead)
    void moveMessages() throws InterruptedException {
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
                subscriberQueue.add(message); // TODO check blocking
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                moveMessages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread interrupted: " + Thread.interrupted());
    }
}
