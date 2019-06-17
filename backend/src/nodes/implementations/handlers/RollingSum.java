package nodes.implementations.handlers;

import nodes.NodeFactory;

import java.util.List;

/**
 * Performs a rolling sum along a configurable-sized window.
 */
public class RollingSum extends RollingOp<Integer> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.ROLLING_SUM, RollingSum::new);
    }

    @Override
    protected Integer executeRollingOp(List<Integer> values) {
        return values.stream()
                .reduce((Integer i1, Integer i2) -> i1 + i2)
                .orElse(null);
    }
}
