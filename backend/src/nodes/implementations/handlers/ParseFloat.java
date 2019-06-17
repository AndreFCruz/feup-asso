package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

public class ParseFloat extends Handler<String, Float> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.PARSE_FLOAT, ParseFloat::new);
    }

    @Override
    public Float handleMessage(String message) throws InterruptedException {
        return Float.parseFloat(message);
    }
}
