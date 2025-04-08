package dev.armenderoian.summad.feature.leaderboard;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.discord.DiscordFeature;
import dev.armenderoian.summad.feature.leaderboard.types.DeathCountLeaderboard;
import dev.armenderoian.summad.feature.leaderboard.types.ObjectiveBackedLeaderboard;
import dev.armenderoian.summad.feature.leaderboard.types.PlayTimeLeaderboard;
import dev.armenderoian.summad.feature.leaderboard.types.StatsBackedLeaderboard;
import dev.armenderoian.summad.util.ServerConfig;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public class LeaderboardFeature extends AbstractFeature {
    private static final Set<LeaderboardUpdater<?>> leaderboardUpdaters = new HashSet<>();
    private static final Set<Leaderboard<?>> leaderboards = new HashSet<>();
    public static StatsBackedLeaderboard.StatsBackedLeaderboardUpdater STATS_UPDATER = registerLeaderboardUpdater(new StatsBackedLeaderboard.StatsBackedLeaderboardUpdater());
    public static ObjectiveBackedLeaderboard.ObjectiveBackedLeaderboardUpdater OBJECTIVE_UPDATER = registerLeaderboardUpdater(new ObjectiveBackedLeaderboard.ObjectiveBackedLeaderboardUpdater());
    public static PlayTimeLeaderboard PLAY_TIME = registerLeaderboard(new PlayTimeLeaderboard(), STATS_UPDATER);
    public static DeathCountLeaderboard DEATH_COUNT = registerLeaderboard(new DeathCountLeaderboard(), OBJECTIVE_UPDATER);

    private ScheduledFuture<?> updateTask;
    private static final int updateInterval = ServerConfig.leaderboardUpdateInterval;

    public LeaderboardFeature(String name) {
        super(name);
    }

    @Override
    public void onStart(MinecraftServer server) throws Exception {
        updateTask = SummerMadness.SCHEDULER.scheduleWithFixedDelay(() -> {
            try {
                for (LeaderboardUpdater<?> updater : leaderboardUpdaters) {
                    updater.updateLeaderboards();
                }

                DiscordFeature.LEADERBOARD_MODULE.updateLeaderboards();
            } catch (Exception e) {
                logger.error("Failed to update leaderboards", e);
            }
        }, 0, updateInterval, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void onStop() throws Exception {
        if (updateTask != null) {
            updateTask.cancel(true);
        }
    }

    public static List<Leaderboard<?>> getLeaderboards() {
        return leaderboards.stream().toList();
    }

    public static int getLeaderboardCount() {
        return leaderboards.size();
    }

    public static <U, T extends LeaderboardUpdater<U>> T registerLeaderboardUpdater(T updater) {
        leaderboardUpdaters.add(updater);
        return updater;
    }

    public static <U, T extends Leaderboard<U>> T registerLeaderboard(T leaderboard, LeaderboardUpdater<U> updater) {
        if (!leaderboardUpdaters.contains(updater))
            throw new IllegalArgumentException("Tried to register a leaderboard with an updater that is not registered");
        if (leaderboards.contains(leaderboard))
            throw new IllegalArgumentException("Tried to register a leaderboard that is already registered");

        updater.registerLeaderboard(leaderboard);
        leaderboards.add(leaderboard);
        return leaderboard;
    }
}
