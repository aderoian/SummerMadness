package dev.armenderoian.summad.util.io.cache;

import java.util.HashMap;
import java.util.Map;

public class GenericDataCache<T, U> implements DataCache<T, U> {

    private final Map<T, U> cache = new HashMap<>();

    @Override
    public void add(T key, U value) {
        cache.put(key, value);
    }

    @Override
    public U get(T key) {
        return cache.get(key);
    }

    @Override
    public void remove(T key) {
        cache.remove(key);
    }

    @Override
    public boolean contains(T key) {
        return cache.containsKey(key);
    }

    @Override
    public Map<T, U> getCache() {
        return cache;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }
}
