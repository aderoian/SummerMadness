package dev.armenderoian.summad.feature.leaderboard.types;

import dev.armenderoian.summad.registry.ModFeatureContent;
import net.minecraft.scoreboard.ScoreboardObjective;

public class DeathCountLeaderboard extends ObjectiveBackedLeaderboard {
    public DeathCountLeaderboard() {
        super("death_count", "Death Count", "How many times players have died.");
    }

    @Override
    protected int compare(LeaderboardEntry entry1, LeaderboardEntry entry2) {
        return Integer.compare(entry2.value(), entry1.value());
    }

    @Override
    protected String formatLine(LeaderboardEntry entry, int lineNumber) {
        return String.valueOf(entry.value());
    }

    @Override
    protected String formatValue(LeaderboardEntry entry) {
        return "Deaths: " + entry.value();
    }

    @Override
    public ScoreboardObjective getObjective() {
        return ModFeatureContent.DEATH_FEATURE.getDeathObjective();
    }
}
