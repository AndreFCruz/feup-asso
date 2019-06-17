package nodes.implementations.handlers;

import nodes.NodeFactory;

import java.util.List;

/**
 * Calculates rolling average along a configurable window.
 */
public class RollingAverage extends RollingOp<Integer> {
    @Override
    protected Integer executeRollingOp(List<Integer> values) {
        Integer sum = values.stream()
                .reduce((Integer i1, Integer i2) -> i1 + i2)
                .orElse(null);
        return sum / values.size();
    }
}
