package dev.armenderoian.summad.feature.leaderboard.types;

public class DeathCountLeaderboard extends ObjectiveBackedLeaderboard {
    public DeathCountLeaderboard() {
        super("death_count", "Death Count", "The players with the lowest amount of deaths.");
    }

    @Override
    protected int compare(LeaderboardEntry entry1, LeaderboardEntry entry2) {
        return -Integer.compare(entry2.value(), entry1.value());
    }

    @Override
    protected String formatLine(LeaderboardEntry entry, int lineNumber) {
        return String.valueOf(entry.value());
    }

    @Override
    protected String formatValue(LeaderboardEntry entry) {
        return "Deaths: " + entry.value();
    }
}
