package dev.armenderoian.summad.feature.leaderboard;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.leaderboard.types.PlayTimeLeaderboard;
import dev.armenderoian.summad.feature.leaderboard.types.StatsBackedLeaderboard;
import dev.armenderoian.summad.util.ServerConfig;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public class LeaderboardFeature extends AbstractFeature {
    private static final Set<LeaderboardUpdater<?>> leaderboardUpdaters = new HashSet<>();
    public static StatsBackedLeaderboard.StatsBackedLeaderboardUpdater STATS_UPDATER = registerLeaderboardUpdater(new StatsBackedLeaderboard.StatsBackedLeaderboardUpdater());
    public static PlayTimeLeaderboard PLAY_TIME = registerLeaderboard(new PlayTimeLeaderboard(), STATS_UPDATER);

    private static int leaderboardCount = 0;

    private ScheduledFuture<?> updateTask;
    private static final int updateInterval = ServerConfig.leaderboardUpdateInterval;

    public LeaderboardFeature(String name) {
        super(name);
    }

    @Override
    public void onStart(MinecraftServer server) throws Exception {
        updateTask = SummerMadness.SCHEDULER.scheduleWithFixedDelay(() -> {
            for (LeaderboardUpdater<?> updater : leaderboardUpdaters) {
                updater.updateLeaderboards();
            }
        }, 0, updateInterval, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void onStop() throws Exception {
        if (updateTask != null) {
            updateTask.cancel(true);
        }
    }

    public static int getLeaderboardCount() {
        return leaderboardCount;
    }

    public static <U, T extends LeaderboardUpdater<U>> T registerLeaderboardUpdater(T updater) {
        leaderboardUpdaters.add(updater);
        return updater;
    }

    public static <U, T extends Leaderboard<U>> T registerLeaderboard(T leaderboard, LeaderboardUpdater<U> updater) {
        updater.registerLeaderboard(leaderboard);
        leaderboardCount++;
        return leaderboard;
    }
}
