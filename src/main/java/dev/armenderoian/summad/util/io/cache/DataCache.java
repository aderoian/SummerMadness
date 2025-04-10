package dev.armenderoian.summad.util.io.cache;

import java.util.Map;

public interface DataCache<T, U> {

    void add(T key, U value);

    U get(T key);

    void remove(T key);

    boolean contains(T key);

    void clear();

    int size();

    Map<T, U> getCache();
}
