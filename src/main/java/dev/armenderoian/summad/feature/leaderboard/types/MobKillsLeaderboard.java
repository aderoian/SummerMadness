package dev.armenderoian.summad.feature.leaderboard.types;

import java.text.NumberFormat;

public class MobKillsLeaderboard extends StatsBackedLeaderboard {

    public MobKillsLeaderboard() {
        super("mob_kills", "Mob Kills", "Total amount of mobs killed by a player.");
    }

    @Override
    protected String getStatsCategory() {
        return "minecraft:killed";
    }

    @Override
    protected String getStatsName() {
        return "";
    }

    @Override
    protected boolean isSummedValue() {
        return true;
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
        return "Mobs: " + NumberFormat.getIntegerInstance().format(entry.value());
    }
}
