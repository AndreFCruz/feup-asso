package nodes.implementations.handlers;

import nodes.Handler;

public class ParseInt extends Handler<String, Integer> {
    @Override
    public Integer handleMessage(String message) throws InterruptedException {
        return Integer.parseInt(message);
    }
}
