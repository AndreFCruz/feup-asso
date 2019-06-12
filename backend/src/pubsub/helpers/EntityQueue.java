package pubsub.helpers;

import java.util.concurrent.BlockingQueue;

public class EntityQueue<MT> {
    public BlockingQueue<MT> queue;
    public String entityId;

    public EntityQueue(String entityId, BlockingQueue<MT> queue) {
        this.entityId = entityId;
        this.queue = queue;
    }
}