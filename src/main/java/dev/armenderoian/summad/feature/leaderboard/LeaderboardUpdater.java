package dev.armenderoian.summad.feature.leaderboard;

import dev.armenderoian.summad.util.io.cache.DataCache;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class LeaderboardUpdater<T> {

    protected final Set<Leaderboard<T>> leaderboards = new HashSet<>();

    public void registerLeaderboard(Leaderboard<T> leaderboard) {
        leaderboards.add(leaderboard);
    }

    public void updateLeaderboards() throws Exception {
        var cache = createCache();
        for (var leaderboard : leaderboards) {
            leaderboard.updateLeaderboard(cache);
        }
    }

    protected abstract DataCache<UUID, T> createCache() throws Exception;
}
