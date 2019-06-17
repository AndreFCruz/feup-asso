package nodes.implementations.handlers;

import nodes.Handler;

/**
 * Converts the given String to uppercase.
 */
public class Uppercase extends Handler<String, String> {
    @Override
    public String handleMessage(String message) {
        return message.toUpperCase();
    }
}
