package nodes.implementations.handlers;

import nodes.NodeFactory;

import java.util.List;

public class OR extends RollingOp<Boolean> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.OR, OR::new);
    }

    @Override
    protected Boolean executeRollingOp(List<Boolean> values) {
        return values.stream().reduce((Boolean b1, Boolean b2) -> b1 || b2).orElse(null);
    }
}
