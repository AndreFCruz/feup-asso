package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Registry<K, T> {
    private Map<K, T> map = new ConcurrentHashMap<>();

    static public <V> Registry<String, V> makeStringRegistry() {
        return new Registry<>() {
            private int counter = 0;

            @Override
            protected String generateKey(V obj) {
                return Integer.toString(counter++);
            }
        };
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

    static public <V> Registry<String, V> makeHashStringRegistry() {
        return new Registry<>() {
            @Override
            protected String generateKey(V obj) {
                return Integer.toString(obj.hashCode());
            }
        };
    }

    protected abstract K generateKey(T obj);

    public K register(T obj) {
        K key = this.generateKey(obj);
        this.map.put(key, obj);
        return key;
    }

    public synchronized T update(K key, T newObject) {
        if (this.map.containsKey(key)) {
            return this.map.put(key, newObject);
        } else {
            return null;
        }
    }

    public T get(K key) {
        return this.map.get(key);
    }

}
