package dev.armenderoian.summad.feature;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.util.PrefixedLogger;
import net.minecraft.server.MinecraftServer;

public abstract class AbstractFeature {

    protected String name;
    protected PrefixedLogger logger;

    public AbstractFeature(String name) {
        this.name = name;
        this.logger = new PrefixedLogger(SummerMadness.LOGGER, name);
    }

    public String getName() {
        return name;
    }

    public PrefixedLogger getLogger() {
        return logger;
    }

    public void registerFeature() throws Exception {
    }

    public void onStart(MinecraftServer server) throws Exception {
    }

    public void onStop() throws Exception {
    }
}
