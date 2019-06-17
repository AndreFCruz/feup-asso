package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

public class Uppercase extends Handler<String, String> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.TO_UPPERCASE, Uppercase::new);
    }

    @Override
    public String handleMessage(String message) {
        return message.toUpperCase();
    }
}
