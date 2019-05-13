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
    class NewMessageEvent {
        int publisherId;

        NewMessageEvent(int publisherId) {
            this.publisherId = publisherId;
        }
    }

    // Registry key(Publisher | Subscriber) -> queue
    private Registry<BlockingQueue<T>> registry = new Registry<BlockingQueue<T>>();

    // publisherKey -> arraySubscriberKeys
    private Map<Integer, ArrayList<Integer>> observers = new HashMap<>();

    private BlockingQueue<NewMessageEvent> eventQueue = new LinkedBlockingQueue<>();

    public Broker() {
    }

    /**
     * Register a new Entity (Publisher | Subscriber)
     */
    public int register(AbstractEntity<T> entity) {
        BlockingQueue<T> queue = new LinkedBlockingQueue<>(); // TODO eventually limit queue size
        int entityId = this.registry.register(queue);
        entity.initializeEntity(entityId, queue, this);
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
    // DEPRECATED
    void moveMessages() throws InterruptedException {
        for (Map.Entry<Integer, ArrayList<Integer>> entry : this.observers.entrySet()) {
            int publisherKey = entry.getKey();
            BlockingQueue<T> publisherQueue = this.registry.get(publisherKey);
            if(publisherQueue.peek() == null) {
                continue;
            }

            T message = publisherQueue.take();

            ArrayList<Integer> subscribers = this.observers.get(publisherKey);
            for (int subscriber : subscribers) {
                BlockingQueue<T> subscriberQueue = this.registry.get(subscriber);
                subscriberQueue.add(message); // TODO check blocking
            }
        }
    }

    /**
     * Method used for publishers to notify the Broker that there's a new message to
     * be handled.
     * 
     * @param publisherId Id of the publishing Publisher
     * @throws InterruptedException
     */
    public void notifyNewMessage(int publisherId) throws InterruptedException {
        eventQueue.put(new NewMessageEvent(publisherId));
    }

    private void handleNewMessageEvent(NewMessageEvent event) {
        BlockingQueue<T> pubQueue = registry.get(event.publisherId);
        T msg = pubQueue.poll();
        if (msg == null) {
            System.err.println("Was notified of inexistent Message");
            return;
        }

        ArrayList<Integer> subscribers = this.observers.get(event.publisherId);
        for (int subId : subscribers) {
            // Offer the published message to all subscribers
            boolean ret = this.registry.get(subId).offer(msg);
            if (!ret) {
                System.out.println("Subscriber " + subId + " could not receive a message (Queue full?)");
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            NewMessageEvent event;
            try {
                event = eventQueue.take(); // .take() blocks awaiting for new Events
            } catch (InterruptedException e1) {
                break;
            }
            this.handleNewMessageEvent(event);

        }
        System.out.println("Thread interrupted: " + Thread.interrupted());
    }
}
