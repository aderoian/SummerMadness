package dev.armenderoian.summad.feature.leaderboard;

import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.leaderboard.types.PlayTimeLeaderboard;
import dev.armenderoian.summad.feature.leaderboard.types.StatsBackedLeaderboard;
import net.minecraft.server.MinecraftServer;

public class LeaderboardFeature extends AbstractFeature {

    public static StatsBackedLeaderboard.StatsBackedLeaderboardUpdater STATS_UPDATER = new StatsBackedLeaderboard.StatsBackedLeaderboardUpdater();

    public static PlayTimeLeaderboard PLAY_TIME = registerLeaderboard(new PlayTimeLeaderboard(), STATS_UPDATER);

    public LeaderboardFeature(String name) {
        super(name);
    }

    @Override
    public void onStart(MinecraftServer server) throws Exception {
        STATS_UPDATER.updateLeaderboards();
        logger.info(PLAY_TIME.toGameMessage(10));
    }

    public static <U, T extends Leaderboard<U>> T registerLeaderboard(T leaderboard, LeaderboardUpdater<U> updater) {
        updater.registerLeaderboard(leaderboard);
        return leaderboard;
    }
}
