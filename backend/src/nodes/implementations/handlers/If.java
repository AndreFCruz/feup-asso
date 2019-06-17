package nodes.implementations.handlers;

import nodes.Handler;

/**
 * If message is False stop signal here, else continue signal to following nodes.
 */
public class If extends Handler<Boolean, Boolean> {
    @Override
    public Boolean handleMessage(Boolean message) throws InterruptedException {
        return message == false ? null : true;
    }
}
