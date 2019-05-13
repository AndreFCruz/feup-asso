package stuff;

public abstract class Publisher<T> extends AbstractEntity<T> {

    public abstract T generateMessage() throws InterruptedException;

    private void publishMessage(T message) {
        queue.add(message);
    }

    @Override
    public void run() {
        double start = System.currentTimeMillis();
        while (start + runTime > System.currentTimeMillis()) {
            T message = null;
            try {
                message = generateMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishMessage(message);
        }
    }
}
