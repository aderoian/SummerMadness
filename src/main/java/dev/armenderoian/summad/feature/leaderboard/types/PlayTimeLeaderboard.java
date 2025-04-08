package dev.armenderoian.summad.feature.leaderboard.types;

public class PlayTimeLeaderboard extends StatsBackedLeaderboard {
    public PlayTimeLeaderboard() {
        super("playtime", "Play Time", "The total amount of play time on the server.");
    }

    @Override
    protected String getStatsCategory() {
        return "minecraft:custom";
    }

    @Override
    protected String getStatsName() {
        return "minecraft:play_time";
    }

    @Override
    protected boolean isSummedValue() {
        return false;
    }

    @Override
    protected int compare(LeaderboardEntry entry1, LeaderboardEntry entry2) {
        return Integer.compare(entry2.value(), entry1.value());
    }

    @Override
    protected String formatLine(LeaderboardEntry entry, int lineNumber) {
        return lineNumber + ": " + entry.value();
    }

    @Override
    protected String formatValue(LeaderboardEntry entry) {
        return String.valueOf(entry.value());
    }
}
