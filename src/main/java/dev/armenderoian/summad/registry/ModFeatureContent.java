package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.combat.CombatLoggerFeature;
import dev.armenderoian.summad.feature.death.DeathFeature;
import dev.armenderoian.summad.feature.discord.DiscordFeature;

import java.util.HashMap;
import java.util.Map;

public class ModFeatureContent {
    private static Map<String, AbstractFeature> features = new HashMap<>();

    public static final DeathFeature DEATH_FEATURE = registerFeature(new DeathFeature("death"));
    public static final CombatLoggerFeature COMBAT_LOGGER_FEATURE = registerFeature(new CombatLoggerFeature("combat_logger"));
    public static final DiscordFeature DISCORD_FEATURE = registerFeature(new DiscordFeature("discord"));

    public static void registerFeatures() {
        SummerMadness.LOGGER.info("Registering mod features for '" + SummerMadness.MOD_ID + "'.");

        features.forEach((name, feature) -> {
            try {
                feature.registerFeature();
                feature.onStart(SummerMadness.SERVER);
                SummerMadness.LOGGER.info("Started feature '" + name + "'.");
            } catch (Exception e) {
                SummerMadness.LOGGER.error("Failed to start feature '" + name + "'.", e);
            }
        });
    }

    public static void onDisable() {
        SummerMadness.LOGGER.info("Disabling mod features for '" + SummerMadness.MOD_ID + "'.");
        features.forEach((name, feature) -> {
            try {
                feature.onStop();
                SummerMadness.LOGGER.info("Stopped feature '" + name + "'.");
            } catch (Exception e) {
                SummerMadness.LOGGER.error("Failed to stop feature '" + name + "'.", e);
            }
        });
    }

    public static <T extends AbstractFeature> T registerFeature(T feature) {
        if (features.containsKey(feature.getName()))
            throw new IllegalArgumentException("Feature '" + feature.getName() + "' is already registered.");
        features.put(feature.getName(), feature);
        return feature;
    }
}
