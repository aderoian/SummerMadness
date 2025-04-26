package dev.armenderoian.summad.feature.leaderboard.types;

import java.text.NumberFormat;

public class PlayerKillsLeaderboard extends StatsBackedLeaderboard {

    public PlayerKillsLeaderboard() {
        super("player_kills", "Player Kills", "To total amount of player opponents killed by a player.");
    }

    @Override
    protected String getStatsCategory() {
        return "minecraft:killed";
    }

    @Override
    protected String getStatsName() {
        return "minecraft:player";
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
        return "Kills: " + NumberFormat.getIntegerInstance().format(entry.value());
    }
}
