package pubsub;

import nodes.Handler;
import nodes.Sink;
import nodes.Source;
import pubsub.helpers.EntityQueue;
import pubsub.helpers.MessageEvent;
import utils.Log;
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
    private static int DEFAULT_QUEUE_CAPACITY = 10;

    private static float BACK_PRESSURE_THRESHOLD = 0.5f;

    // Registry key(Source | Sink | inputHandler | outputHandler) -> queue
    private Registry<String, BlockingQueue<MT>> registry = Registry.makeStringRegistry();
    // publisherKey(Source | InputHandler) -> arraySubscriberKeys (Sink | OutputHandler)
    private Map<String, ArrayList<String>> observers = new HashMap<>();
    private BlockingQueue<MessageEvent> eventQueue = new LinkedBlockingQueue<>();

    public Broker() {
    }

    /**
     * Register a new Entity
     * @return string key
     */
    public String register(Source source) {
        return source.initialize(this.registerEntity(), this);
    }

    public String register(Sink sink) {
        return sink.initialize(this.registerEntity());
    }

    public String register(Handler handler) {
        EntityQueue entityQueuePublish = this.registerEntity();
        EntityQueue entityQueueSubscribe = this.registerEntity();
        return handler.initialize(entityQueueSubscribe, entityQueuePublish, this);
    }

    private EntityQueue registerEntity() {
        BlockingQueue<MT> queue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
        String entityId = this.registry.register(queue);
        return new EntityQueue(entityId, queue);
    }

    public void addSubscriber(String subscriberId, String publisherId) {
        ArrayList<String> subscribersList = this.observers.getOrDefault(publisherId, new ArrayList<>());
        subscribersList.add(subscriberId);
        this.observers.put(publisherId, subscribersList);
    }

    /**
     * Sets the buffer size of the given Publisher to a new value.
     * This serves as a mechanism for applying back pressure, as the publisher will block
     *  until its queue has available slots before continuing to publish new messages.
     * @param pubId         the Publisher's ID.
     * @param bufferSize    the new buffer size.
     * @return Whether the operation was successful.
     */
    public boolean setBufferSize(String pubId, int bufferSize) {
        BlockingQueue<MT> oldQueue = registry.get(pubId);
        BlockingQueue<MT> newQueue = new LinkedBlockingQueue<>(bufferSize);
        oldQueue.drainTo(newQueue, bufferSize);

        return registry.update(pubId, newQueue) != null;
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
            Log.logError("Was notified of inexistent Message");
            return;
        }

        assert msg.hashCode() == event.messageHash;
        if (!event.isAlive()) {
            Log.logWarning("Discarded message with expired Time-to-Live");
            return;
        }

        int subsAtMaxCapacity = 0;
        ArrayList<String> subscribers = this.observers.getOrDefault(event.publisherId, new ArrayList<>());
        for (String subId : subscribers) {
            // Offer the published message to all subscribers
            BlockingQueue<MT> subQueue = this.registry.get(subId);

            if (subQueue.remainingCapacity() == 0) {
                // Discard oldest message if queue is at maximum capacity
                subQueue.remove();
                subsAtMaxCapacity++;
            }

            boolean success = subQueue.offer(msg);
            if (!success) {
                Log.logWarning("Subscriber " + subId + " could not receive a message");
            }
        }

        // Check if subscribers are under pressure, and potentially apply back pressure
        if (((float) subsAtMaxCapacity / subscribers.size()) >= BACK_PRESSURE_THRESHOLD) {
            Log.log("Applying back pressure to publisher '" + event.publisherId + "'.");
            this.setBufferSize(event.publisherId, (pubQueue.size() / 2) + 1);
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
        Log.log("[Broker] Thread interrupted");
    }
}
