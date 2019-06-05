package nodes.implementations.sinks;

import nodes.Sink;

public class FileWriter extends Sink<Object, Void> {

    // May block when handling message
    @Override
    public Void handleMessage(Object message) {
        System.out.println("Sink id " + this.getId() + " | Received the message: " + message);
        return null;
    }
}
