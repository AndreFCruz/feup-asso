package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

public class PredictFloat extends Handler<Float, Float> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.PREDICT_FLOAT, PredictFloat::new);
    }

    PredictFloat() {

    }

    // TODO
    @Override
    public Float handleMessage(Float message) throws InterruptedException {

        return null;
    }
}
