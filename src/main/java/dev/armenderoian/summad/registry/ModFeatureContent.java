package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.FeatureRegistry;
import dev.armenderoian.summad.feature.combat.CombatLoggerFeature;
import dev.armenderoian.summad.feature.death.DeathFeature;
import dev.armenderoian.summad.feature.discord.DiscordFeature;
import dev.armenderoian.summad.feature.leaderboard.LeaderboardFeature;
import dev.armenderoian.summad.util.io.cache.KnownPlayerCache;

public class ModFeatureContent {
    private static final FeatureRegistry<AbstractFeature> featureRegistry = new FeatureRegistry<>();

    public static final DeathFeature DEATH_FEATURE = registerFeature(new DeathFeature("death"));
    public static final CombatLoggerFeature COMBAT_LOGGER_FEATURE = registerFeature(new CombatLoggerFeature("combat_logger"));
    public static final DiscordFeature DISCORD_FEATURE = registerFeature(new DiscordFeature("discord"));
    public static final LeaderboardFeature LEADERBOARD_FEATURE = registerFeature(new LeaderboardFeature("leaderboard"), "death", "discord");

    public static void registerFeatures() throws Exception {
        SummerMadness.LOGGER.info("Registering mod features for '" + SummerMadness.MOD_ID + "'.");

        KnownPlayerCache.initCache();

        featureRegistry.registerFeatures(SummerMadness.SERVER);
    }

    public static void onDisable() {
        SummerMadness.LOGGER.info("Disabling mod features for '" + SummerMadness.MOD_ID + "'.");
        featureRegistry.getLoaded().forEach((name, feature) -> {
            try {
                feature.onStop();
                SummerMadness.LOGGER.info("Stopped feature '" + name + "'.");
            } catch (Exception e) {
                SummerMadness.LOGGER.error("Failed to stop feature '" + name + "'.", e);
            }
        });
    }

    public static <T extends AbstractFeature> T registerFeature(T feature) {
        return featureRegistry.registerFeature(feature.getName(), feature);
    }

    public static <T extends AbstractFeature> T registerFeature(T feature, String... dependencies) {
        return featureRegistry.registerFeature(feature.getName(), feature, dependencies);
    }
}
