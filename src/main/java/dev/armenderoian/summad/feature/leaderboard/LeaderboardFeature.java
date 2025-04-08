package dev.armenderoian.summad.feature.leaderboard;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.leaderboard.types.PlayTimeLeaderboard;
import dev.armenderoian.summad.feature.leaderboard.types.StatsBackedLeaderboard;
import dev.armenderoian.summad.util.ServerConfig;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LeaderboardFeature extends AbstractFeature {
    private static final Set<LeaderboardUpdater<?>> leaderboardUpdaters = new HashSet<>();
    public static StatsBackedLeaderboard.StatsBackedLeaderboardUpdater STATS_UPDATER = registerLeaderboardUpdater(new StatsBackedLeaderboard.StatsBackedLeaderboardUpdater());
    public static PlayTimeLeaderboard PLAY_TIME = registerLeaderboard(new PlayTimeLeaderboard(), STATS_UPDATER);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int updateInterval = ServerConfig.leaderboardUpdateInterval;

    public LeaderboardFeature(String name) {
        super(name);
    }

    @Override
    public void onStart(MinecraftServer server) throws Exception {
        scheduler.scheduleAtFixedRate(() -> {
            for (LeaderboardUpdater<?> updater : leaderboardUpdaters) {
                updater.updateLeaderboards();
            }
        }, 0, updateInterval, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void onStop() throws Exception {
        scheduler.shutdownNow();
    }

    public static <U, T extends LeaderboardUpdater<U>> T registerLeaderboardUpdater(T updater) {
        leaderboardUpdaters.add(updater);
        return updater;
    }

    public static <U, T extends Leaderboard<U>> T registerLeaderboard(T leaderboard, LeaderboardUpdater<U> updater) {
        updater.registerLeaderboard(leaderboard);
        return leaderboard;
    }
}
