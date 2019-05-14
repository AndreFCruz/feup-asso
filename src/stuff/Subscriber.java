package stuff;

public interface Subscriber<T> extends Entity<T> {


    /**
     * Handle the given Message
     * May block!
     * @param message the message
     * @throws InterruptedException thrown when interrupted
     */
    void handleMessage(T message) throws InterruptedException;

    /**
     * May block if Queue is empty
     * @return The pulled message
     * @throws InterruptedException thrown when interrupted
     */
    private T pullMessage() throws InterruptedException {
        return this.getQueue().take();
    }

    @Override
    default void run() {
        try {
            while (! Thread.interrupted()) {
                T msg = this.pullMessage();
                this.handleMessage(msg);
            }
        } catch (InterruptedException e) {
            System.out.println("Subscriber " + getId() + " Thread interrupted");
        }
    }
}
