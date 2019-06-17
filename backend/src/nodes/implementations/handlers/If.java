package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

/**
 * If message is False stop signal here, else continue signal to following nodes.
 */
public class If extends Handler<Boolean, Boolean> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.IF, If::new);
    }

    @Override
    public Boolean handleMessage(Boolean message) throws InterruptedException {
        return message == false ? null : true;
    }
}
