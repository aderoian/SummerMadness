package dev.armenderoian.summad.feature.leaderboard.types;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.leaderboard.LeaderboardUpdater;
import dev.armenderoian.summad.registry.ModFeatureContent;
import dev.armenderoian.summad.util.cache.DataCache;
import dev.armenderoian.summad.util.cache.GenericDataCache;
import dev.armenderoian.summad.util.cache.KnownPlayerCache;
import net.minecraft.scoreboard.ScoreAccess;

import java.util.UUID;

public abstract class ObjectiveBackedLeaderboard extends AbstractLeaderboard<ScoreAccess> {
    public ObjectiveBackedLeaderboard(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    protected int loadValueForPlayer(UUID uuid, DataCache<UUID, ScoreAccess> cache) {
        return cache.contains(uuid) ? cache.get(uuid).getScore() : 0;
    }

    public static DataCache<UUID, ScoreAccess> createObjectiveCache() throws Exception {
        var logger = SummerMadness.LOGGER;

        var cache = new GenericDataCache<UUID, ScoreAccess>();
        var objective = ModFeatureContent.DEATH_FEATURE.getDeathObjective();
        var scoreboard = objective.getScoreboard();
        KnownPlayerCache.getKnownPlayers().forEach(player -> {
            var score = scoreboard.getOrCreateScore(player::name, objective);
            cache.add(player.uuid(), score);
        });
        return cache;
    }

    public static class ObjectiveBackedLeaderboardUpdater extends LeaderboardUpdater<ScoreAccess> {

        @Override
        protected DataCache<UUID, ScoreAccess> createCache() throws Exception {
            return createObjectiveCache();
        }
    }
}
