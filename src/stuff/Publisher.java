package stuff;

public abstract class Publisher<T> extends AbstractEntity<T> {
    
    // May block
    public abstract T getMessage() throws InterruptedException;

    // May block if queue is full
    private void publishMessage(T message) throws InterruptedException {
        queue.put(message);
        broker.notifyNewMessage(this.getId());
    }

    @Override
    public void run() {
        double start = System.currentTimeMillis();
        while (start + runTime > System.currentTimeMillis()) {
            T message = null;
            try {
                message = getMessage();
                publishMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
