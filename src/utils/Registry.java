package utils;

public class Registry<T> extends AbstractRegistry<Integer, T> {

    private int counter = 0;

    @Override
    protected Integer generateKey(T obj) {
        return counter++;
    }
}
