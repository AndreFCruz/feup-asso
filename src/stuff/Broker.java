package stuff;

import utils.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @param <T> Message type
 */
public class Broker<T> implements Runnable {
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
    public int register(Entity<T> entity) {
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
        if (msg == null) {
            System.err.println("Was notified of inexistent Message");
            return;
        }

        assert msg.hashCode() == event.messageHash;
        if (!event.isAlive()) {
            System.out.println("Discarded message with expired Time-to-Live");
            return;
        }

        ArrayList<Integer> subscribers = this.observers.get(event.publisherId);
        for (int subId : subscribers) {
            // Offer the published message to all subscribers
            boolean ret = this.registry.get(subId).offer(msg);
            if (!ret) {
                System.out.println("Subscriber " + subId + " could not receive a message (Queue full?)");
                // TODO if queue is full take oldest message and publish most recent ?
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

    class MessageEvent {
        int publisherId;
        int messageHash;
        Long timeToLive;    // In milliseconds
        long arrivalTime;   // In milliseconds

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
            return this.timeToLive == null ||
                    this.arrivalTime + this.timeToLive > System.currentTimeMillis();

        }
    }
}
