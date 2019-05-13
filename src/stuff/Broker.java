package stuff;

import utils.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Broker<T> implements Runnable {

    // Registry key(Publisher | Subscriber) -> queue
    private Registry<BlockingQueue<T>> registry = new Registry<>();
    // publisherKey -> arraySubscriberKeys
    private Map<Integer, ArrayList<Integer>> observers = new HashMap<>();

    public Broker() {
    }

    public int addPublisher(Publisher<T> obj) {
        return this.initializeEntity(obj);
    }

    public void addSubscriber(Subscriber<T> obj, int publisherId) {
        int key = this.initializeEntity(obj);
        ArrayList<Integer> subscribersList = new ArrayList<>();
        if (this.observers.containsKey(publisherId)) {
            subscribersList = this.observers.get(publisherId);
        }
        subscribersList.add(key);
        this.observers.put(publisherId, subscribersList);
    }

    private int initializeEntity(AbstractEntity<T> obj) {
        BlockingQueue<T> queue = new LinkedBlockingQueue<>();
        Object key = registry.register(queue);
        obj.initVariables(queue);
        return (int) key;
    }

    private void movesMessages() throws InterruptedException {
        for (Map.Entry<Integer, ArrayList<Integer>> entry : this.observers.entrySet()) {
            int publisherKey = entry.getKey();
            BlockingQueue<T> publisherQueue = this.registry.get(publisherKey);
            if (publisherQueue.peek() != null) {
                T message = publisherQueue.take();

                ArrayList<Integer> subscribers = this.observers.get(publisherKey);
                for (int subscriber : subscribers) {
                    BlockingQueue<T> subscriberQueue = this.registry.get(subscriber);
                    System.out.println(subscriber);
                    subscriberQueue.add(message);
                }
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                movesMessages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread interrupted: " + Thread.interrupted());
    }
}
