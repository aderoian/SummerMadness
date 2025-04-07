package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.death.DeathFeature;

public class ModFeatureContent {

    public static void registerFeatures() {
        SummerMadness.LOGGER.info("Registering mod features for '" + SummerMadness.MOD_ID + "'.");

        DeathFeature.registerDeathFeature();
    }
}
