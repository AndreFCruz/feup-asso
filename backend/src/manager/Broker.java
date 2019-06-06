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
 * @param <T> Message type
 */
public class Broker<T> implements Runnable {
    // Registry key(Source | Sink | inputHandler | outputHandler) -> queue
    private Registry<Integer, BlockingQueue<T>> registry = Registry.makeIntRegistry();
    // publisherKey(Source | InputHandler) -> arraySubscriberKeys (Sink | OutputHandler)
    private Map<Integer, ArrayList<Integer>> observers = new HashMap<>();
    private BlockingQueue<MessageEvent> eventQueue = new LinkedBlockingQueue<>();

    public Broker() {
    }

    /**
     * Register a new Entity
     *
     * @return
     */
    public String register(Source source) {
        EntityQueue entityQueue = this.registerEntity();
        return source.initialize(entityQueue.entityId, entityQueue.queue, this);
    }

    public String register(Sink sink) {
        EntityQueue entityQueue = this.registerEntity();
        return sink.initialize(entityQueue.entityId, entityQueue.queue);
    }

    public String register(Handler handler) {
        EntityQueue entityQueuePublish = this.registerEntity();
        EntityQueue entityQueueSubscribe = this.registerEntity();
        return handler.initialize(entityQueuePublish.entityId, entityQueuePublish.queue, this, entityQueueSubscribe.entityId, entityQueueSubscribe.queue);
    }

    private EntityQueue registerEntity() {
        BlockingQueue<T> queue = new LinkedBlockingQueue<>(); // TODO eventually limit queue size
        int entityId = this.registry.register(queue);
        return new EntityQueue(entityId, queue);
    }

    public void addSubscriber(String subscriberId, String publisherId) {
        ArrayList<Integer> subscribersList = this.observers.getOrDefault(Integer.parseInt(publisherId), new ArrayList<>());
        subscribersList.add(Integer.parseInt(subscriberId));
        this.observers.put(Integer.parseInt(publisherId), subscribersList);
    }

    /**
     * Method used for publishers to notify the Broker that there's a new message to
     * be handled.
     *
     * @param publisherId Id of the publishing Publisher
     * @param messageHash Hash of the message
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

    class EntityQueue {
        BlockingQueue<T> queue;
        int entityId;

        EntityQueue(int entityId, BlockingQueue<T> queue) {
            this.entityId = entityId;
            this.queue = queue;
        }
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
