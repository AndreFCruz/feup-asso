package pubsub;

public interface Subscriber<In, Ret> {

    /**
     * Handle the given Message
     * May block!
     *
     * @param message the message
     * @return The handled message, if there's anything to return.
     * @throws InterruptedException thrown when interrupted
     */
    Ret handleMessage(In message) throws InterruptedException;
}
