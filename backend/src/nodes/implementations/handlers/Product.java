package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

public class Product extends Handler<Integer, Integer> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.PAIRWISE_PRODUCT, Product::new);
    }

    Integer previousValue = 1;

    @Override
    public Integer handleMessage(Integer currentValue) throws InterruptedException {
        Integer ret = previousValue * currentValue;
        previousValue = currentValue;
        return ret;
    }
}
