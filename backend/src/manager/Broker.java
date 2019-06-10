package manager;

import nodes.Handler;
import nodes.Sink;
import nodes.Source;
import utils.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Broker class for mediating message exchange between nodes.
 * Note: this Broker uses the String type for Node's IDs.
 *
 * @param <MT> Message Type
 */
public class Broker<MT> implements Runnable {
    // Registry key(Source | Sink | inputHandler | outputHandler) -> queue
    private Registry<String, BlockingQueue<MT>> registry = Registry.makeStringRegistry();
    // publisherKey(Source | InputHandler) -> arraySubscriberKeys (Sink | OutputHandler)
    private Map<String, ArrayList<String>> observers = new HashMap<>();
    private BlockingQueue<MessageEvent> eventQueue = new LinkedBlockingQueue<>();

    public Broker() {
    }

    /**
     * Register a new Entity
     *
     * @return string key
     */
    String register(Source source) {
        EntityQueue entityQueue = this.registerEntity();
        return source.initialize(entityQueue.entityId, entityQueue.queue, this);
    }

    String register(Sink sink) {
        EntityQueue entityQueue = this.registerEntity();
        return sink.initialize(entityQueue.entityId, entityQueue.queue);
    }

    String register(Handler handler) {
        EntityQueue entityQueuePublish = this.registerEntity();
        EntityQueue entityQueueSubscribe = this.registerEntity();
        return handler.initialize(entityQueueSubscribe.entityId, entityQueueSubscribe.queue, entityQueuePublish.entityId, entityQueuePublish.queue, this);
    }

    private EntityQueue registerEntity() {
        BlockingQueue<MT> queue = new LinkedBlockingQueue<>(); // TODO eventually limit queue size
        String entityId = this.registry.register(queue);
        return new EntityQueue(entityId, queue);
    }

    void addSubscriber(String subscriberId, String publisherId) {
        ArrayList<String> subscribersList = this.observers.getOrDefault(publisherId, new ArrayList<>());
        subscribersList.add(subscriberId);
        this.observers.put(publisherId, subscribersList);
    }

    /**
     * Method used for publishers to notify the Broker that there's a new message to
     * be handled.
     *
     * @param publisherId Id of the publishing Publisher
     * @param messageHash Hash of the message
     */
    public void notifyNewMessage(String publisherId, int messageHash) throws InterruptedException {
        eventQueue.put(new MessageEvent(publisherId, messageHash));
    }

    private void handleMessageEvent(MessageEvent event) {
        BlockingQueue<MT> pubQueue = registry.get(event.publisherId);
        MT msg = pubQueue.poll();
        if (msg == null) {
            System.err.println("Was notified of inexistent Message");
            return;
        }

        assert msg.hashCode() == event.messageHash;
        if (!event.isAlive()) {
            System.out.println("Discarded message with expired Time-to-Live");
            return;
        }

        ArrayList<String> subscribers = this.observers.getOrDefault(event.publisherId, new ArrayList<>());
        for (String subId : subscribers) {
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
            } catch (InterruptedException e) {
                break;
            }
            this.handleMessageEvent(event);
        }
        System.out.println("Thread interrupted: " + Thread.interrupted());
    }

    class EntityQueue {
        BlockingQueue<MT> queue;
        String entityId;

        EntityQueue(String entityId, BlockingQueue<MT> queue) {
            this.entityId = entityId;
            this.queue = queue;
        }
    }

    class MessageEvent {
        String publisherId;
        int messageHash;
        Long timeToLive;    // In milliseconds
        long arrivalTime;   // In milliseconds

        MessageEvent(String publisherId, int messageHash) {
            this(publisherId, messageHash, null);
        }

        MessageEvent(String publisherId, int messageHash, Long timeToLive) {
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
