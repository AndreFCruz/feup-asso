package utils;

import java.util.HashMap;
import java.util.Map;

public abstract class Registry<K, T> {
    private Map<K, T> map = new HashMap<>();

    protected abstract K generateKey(T obj);

    public K register(T obj) {
        K key = this.generateKey(obj);
        this.map.put(key, obj);
        return key;
    }

    public T get(K key) {
        return this.map.get(key);
    }

    static public <V> Registry<Integer, V> makeIntRegistry() {
        return new Registry<>() {
            private int counter = 0;

            @Override
            protected Integer generateKey(V obj) {
                return counter++;
            }
        };
    }
}
