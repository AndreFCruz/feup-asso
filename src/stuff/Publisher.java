package stuff;


public interface Publisher<T> extends Entity<T> {
    
    /**
     * Method for this Publisher to generate a Message.
     * May block!
     * @return The generated Message
     * @throws InterruptedException Thrown when interrupted
     */
    T getMessage() throws InterruptedException;

    /**
     * Private method used for publishing new messages
     * @param message Message to publish
     * @throws InterruptedException Thrown when interrupted
     */
    private void publishMessage(T message) throws InterruptedException {
        this.getQueue().put(message);
        getBroker().notifyNewMessage(this.getId(), message.hashCode());
    }

    @Override
    default void run() {
        try {
            while (! Thread.interrupted()) {
                T message = this.getMessage();
                this.publishMessage(message);
            }
        } catch (InterruptedException e) {
            System.out.println("Publisher " + getId() + " Thread interrupted");
        }
    }
 
}