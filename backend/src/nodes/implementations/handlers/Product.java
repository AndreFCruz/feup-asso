package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

/**
 * Calculates pairwise product between consecutive numbers.
 */
public class Product extends Handler<Integer, Integer> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.PAIRWISE_PRODUCT, Product::new);
    }

    private Integer previousValue = 1;

    @Override
    public Integer handleMessage(Integer currentValue) throws InterruptedException {
        Integer ret = previousValue * currentValue;
        previousValue = currentValue;
        return ret;
    }
}
