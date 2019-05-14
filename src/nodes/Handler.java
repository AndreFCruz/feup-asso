package nodes;

import stuff.AbstractEntity;
import stuff.Publisher;
import stuff.Subscriber;

abstract class Handler<T> extends AbstractEntity<T> implements Subscriber<T>, Publisher<T> {

    @Override
    public void run() {
        // TODO
    }
}