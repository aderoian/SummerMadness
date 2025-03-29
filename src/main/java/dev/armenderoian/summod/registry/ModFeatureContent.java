package dev.armenderoian.summod.registry;

import dev.armenderoian.summod.SummerModded;
import dev.armenderoian.summod.feature.death.DeathFeature;

public class ModFeatureContent {

    public static void registerFeatures() {
        SummerModded.LOGGER.info("Registering mod features for '" + SummerModded.MOD_ID + "'.");

        DeathFeature.registerDeathFeature();
    }
}
