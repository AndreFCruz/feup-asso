package nodes.implementations.sinks;

import nodes.Sink;

public class FileWriter extends Sink<Object> {

    // May block when handling message
    @Override
    protected void handleMessage(Object message) {
        System.out.println("Sink id " + this.id + " | Received the message: " + message);
    }
}
