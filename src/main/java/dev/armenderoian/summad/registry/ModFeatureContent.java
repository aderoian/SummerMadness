package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.death.DeathFeature;
import dev.armenderoian.summad.feature.discord.DiscordFeature;

public class ModFeatureContent {

    public static void registerFeatures() {
        SummerMadness.LOGGER.info("Registering mod features for '" + SummerMadness.MOD_ID + "'.");

        DeathFeature.registerDeathFeature();
        DiscordFeature.registerDiscordFeature();
    }

    public static void onDisable() {
        SummerMadness.LOGGER.info("Disabling mod features for '" + SummerMadness.MOD_ID + "'.");

        DiscordFeature.onDisable();
    }
}
