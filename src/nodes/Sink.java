package nodes;

import stuff.AbstractEntity;
import stuff.Subscriber;

abstract class Sink<T> extends AbstractEntity<T> implements Subscriber<T> {

}