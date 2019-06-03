package nodes.implementations.handlers;

import nodes.Handler;

public class Uppercase extends Handler<String, String> {

    @Override
    protected String handleMessage(String message) {
        return message.toUpperCase();
    }
}