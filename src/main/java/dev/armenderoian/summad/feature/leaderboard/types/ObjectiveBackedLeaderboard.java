package dev.armenderoian.summad.feature.leaderboard.types;

import dev.armenderoian.summad.feature.leaderboard.Leaderboard;
import dev.armenderoian.summad.feature.leaderboard.LeaderboardUpdater;
import dev.armenderoian.summad.util.io.cache.DataCache;
import dev.armenderoian.summad.util.io.cache.GenericDataCache;
import dev.armenderoian.summad.util.io.cache.KnownPlayerCache;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreboardObjective;

import java.util.UUID;

public abstract class ObjectiveBackedLeaderboard extends AbstractLeaderboard<ScoreAccess> {
    public ObjectiveBackedLeaderboard(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    protected int loadValueForPlayer(UUID uuid, DataCache<UUID, ScoreAccess> cache) {
        return cache.contains(uuid) ? cache.get(uuid).getScore() : 0;
    }

    public DataCache<UUID, ScoreAccess> createObjectiveCache() throws Exception {
        var cache = new GenericDataCache<UUID, ScoreAccess>();
        var objective = getObjective();
        var scoreboard = objective.getScoreboard();
        KnownPlayerCache.getKnownPlayers().forEach(player -> {
            var score = scoreboard.getOrCreateScore(player::name, objective);
            cache.add(player.uuid(), score);
        });
        return cache;
    }

    public abstract ScoreboardObjective getObjective();

    public static class ObjectiveBackedLeaderboardUpdater extends LeaderboardUpdater<ScoreAccess> {

        @Override
        public void registerLeaderboard(Leaderboard<ScoreAccess> leaderboard) {
            if (!(leaderboard instanceof ObjectiveBackedLeaderboard)) {
                throw new IllegalArgumentException("Leaderboard must be an instance of ObjectiveBackedLeaderboard");
            }
            super.registerLeaderboard(leaderboard);
        }

        @Override
        protected DataCache<UUID, ScoreAccess> createCache() throws Exception {
            return ((ObjectiveBackedLeaderboard) leaderboards.stream().findFirst().get()).createObjectiveCache();
        }
    }
}
