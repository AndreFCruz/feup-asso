package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

/**
 * Converts the given String to uppercase.
 */
public class Uppercase extends Handler<String, String> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.TO_UPPERCASE, Uppercase::new);
    }

    @Override
    public String handleMessage(String message) {
        return message.toUpperCase();
    }
}
