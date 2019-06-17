package nodes.implementations.handlers;

import nodes.NodeFactory;

import java.util.List;

public class RollingAverage extends RollingOp<Integer> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.ROLLING_AVERAGE, RollingAverage::new);
    }

    @Override
    protected Integer executeRollingOp(List<Integer> values) {
        Integer sum = values.stream()
                .reduce((Integer i1, Integer i2) -> i1 + i2)
                .orElse(null);
        return sum / values.size();
    }
}
