package dev.armenderoian.summad.feature;

import dev.armenderoian.summad.SummerMadness;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class FeatureRegistry<T extends AbstractFeature>{

    private final Map<String, T> registry = new HashMap<>();
    private final Map<String, T> loaded = new HashMap<>();
    private final Map<String, Set<String>> dependencies = new HashMap<>();

    public <U extends T> U registerFeature(String name, U feature) {
        if (registry.containsKey(name)) {
            throw new IllegalArgumentException("Feature '" + name + "' is already registered.");
        }
        registry.put(name, feature);
        return feature;
    }

    public <U extends T> U registerFeature(String name, U feature, String ...dependency) {
        if (registry.containsKey(name)) {
            throw new IllegalArgumentException("Feature '" + name + "' is already registered.");
        }
        registry.put(name, feature);
        dependencies.put(name, Set.of(dependency));
        return feature;
    }

    public T getFeature(String name) {
        return registry.get(name);
    }

    public Map<String, T> getRegistry() {
        return registry;
    }

    public Map<String, T> getLoaded() {
        return loaded;
    }

    public void registerFeatures(MinecraftServer server) throws Exception {
        var loaded = new ArrayList<String>();
        var deferred = new HashMap<String, T>();
        var failed = new ArrayList<String>();

        int iterations = 0;
        while (loaded.size() < registry.size() && iterations < registry.size() + 1) {
            for (var entry : registry.entrySet()) {
                var name = entry.getKey();
                var feature = entry.getValue();

                if (loaded.contains(name) || deferred.containsKey(name)) {
                    continue;
                }

                var deps = dependencies.get(name);
                if (deps == null || loaded.containsAll(deps)) {
                    try {
                        feature.registerFeature();
                        loaded.add(name);
                        SummerMadness.LOGGER.info("Registered feature '{}'.", name);
                    } catch (Exception e) {
                        failed.add(name);
                        SummerMadness.LOGGER.error("Failed to register feature '{}'.", name, e);
                    }
                } else {
                    deferred.put(name, feature);
                    SummerMadness.LOGGER.info("Deferred registration of feature '{}' due to dependencies.", name);
                }
            }

            for (var entry : deferred.entrySet()) {
                var name = entry.getKey();
                var feature = entry.getValue();

                if (loaded.contains(name)) {
                    deferred.remove(name);
                    continue;
                }

                var deps = dependencies.get(name);
                if (deps == null) continue;
                for (String d : deps) {
                    if (failed.contains(d)) {
                        deferred.remove(d);
                        failed.add(name);
                        SummerMadness.LOGGER.error("Failed to register feature '{}' due to failed dependency '{}'.", name, d);
                    }
                }
                if (loaded.containsAll(deps)) {
                    try {
                        feature.registerFeature();
                        loaded.add(name);
                        SummerMadness.LOGGER.info("Registered feature '{}'.", name);
                    } catch (Exception e) {
                        failed.add(name);
                        SummerMadness.LOGGER.error("Failed to register feature '{}'.", name, e);
                    }

                    deferred.remove(name);
                }
            }

            iterations++;
        }

        if (iterations >= registry.size() + 1) {
            throw new IllegalStateException("Circular dependency detected in features: " + deferred.keySet());
        }
        if (!deferred.isEmpty()) {
            throw new IllegalStateException("Failed to load all features: " + deferred.keySet());
        }

        for (var name : loaded) {
            var feature = registry.get(name);
            if (feature != null) {
                try {
                    feature.onStart(server);
                    this.loaded.put(name, feature);
                } catch (Exception e) {
                    SummerMadness.LOGGER.error("Failed to start feature '{}'.", name, e);
                }
            }
        }
    }
}
