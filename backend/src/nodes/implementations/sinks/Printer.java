package nodes.implementations.sinks;

import nodes.NodeFactory;
import nodes.Sink;

public class Printer extends Sink.EndSink<Object> {
    static {
        NodeFactory.registerNode(NodeFactory.SinkType.PRINTER, Printer::new);
    }

    // May block when handling message
    @Override
    public Void handleMessage(Object message) {
        System.out.println("Sink id " + this.getId() + " | Received the message: " + message);
        return null;
    }
}
