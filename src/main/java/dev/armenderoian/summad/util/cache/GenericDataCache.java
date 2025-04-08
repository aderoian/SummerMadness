package dev.armenderoian.summad.util.cache;

import java.util.HashMap;
import java.util.Map;

public class GenericDataCache<T, U> {

    private final Map<T, U> cache = new HashMap<>();

    public void add(T key, U value) {
        cache.put(key, value);
    }

    public U get(T key) {
        return cache.get(key);
    }

    public void remove(T key) {
        cache.remove(key);
    }

    public boolean contains(T key) {
        return cache.containsKey(key);
    }

    public Map<T, U> getCache() {
        return cache;
    }

    public void clear() {
        cache.clear();
    }
}
