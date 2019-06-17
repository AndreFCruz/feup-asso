package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

public class ToString extends Handler<Object, String> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.TO_STRING, ToString::new);
    }

    @Override
    public String handleMessage(Object message) throws InterruptedException {
        return message.toString();
    }
}
