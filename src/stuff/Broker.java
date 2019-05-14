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
    class MessageEvent {
        int publisherId;
        int messageHash;
        Long timeToLive;
        long arrivalTime;

        MessageEvent(int publisherId, int messageHash) {
            this(publisherId, messageHash, null);
        }

        MessageEvent(int publisherId, int messageHash, Long timeToLive) {
            this.publisherId = publisherId;
            this.messageHash = messageHash;
            this.timeToLive = timeToLive;
            this.arrivalTime = System.currentTimeMillis();
        }

        boolean isAlive() {
            if (this.timeToLive == null ||
                this.arrivalTime + this.timeToLive > System.currentTimeMillis()) {
                return true;
            } else {
                return false;
            }
        }
    }

    // Registry key(Publisher | Subscriber) -> queue
    private Registry<BlockingQueue<T>> registry = new Registry<BlockingQueue<T>>();

    // publisherKey -> arraySubscriberKeys
    private Map<Integer, ArrayList<Integer>> observers = new HashMap<>();

    private BlockingQueue<MessageEvent> eventQueue = new LinkedBlockingQueue<>();

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
    public void notifyNewMessage(int publisherId, int messageHash) throws InterruptedException {
        eventQueue.put(new MessageEvent(publisherId, messageHash));
    }

    private void handleMessageEvent(MessageEvent event) {
        BlockingQueue<T> pubQueue = registry.get(event.publisherId);
        T msg = pubQueue.poll();
        assert msg.hashCode() == event.messageHash;
        if (msg == null) {
            System.err.println("Was notified of inexistent Message");
            return;
        }
        if (! event.isAlive()) {
            System.out.println("Discarded message with expired Time-to-Live");
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
            MessageEvent event;
            try {
                event = eventQueue.take(); // .take() blocks awaiting for new Events
            } catch (InterruptedException e1) {
                break;
            }
            this.handleMessageEvent(event);

        }
        System.out.println("Thread interrupted: " + Thread.interrupted());
    }
}
