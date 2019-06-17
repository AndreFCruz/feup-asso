package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

public class ParseInt extends Handler<String, Integer> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.PARSE_INT, ParseInt::new);
    }

    @Override
    public Integer handleMessage(String message) throws InterruptedException {
        return Integer.parseInt(message);
    }
}
