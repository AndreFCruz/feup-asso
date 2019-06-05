package pubsub;

public interface Publisher<Out> {

    /**
     * Method for this Source to generate a Message.
     * May block!
     *
     * @return The generated Message
     * @throws InterruptedException Thrown when interrupted
     */
    Out produceMessage() throws InterruptedException;
}
