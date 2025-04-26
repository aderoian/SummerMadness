package dev.armenderoian.summad.feature.leaderboard.types;

import java.text.NumberFormat;

public class ItemsUsedLeaderboard extends StatsBackedLeaderboard {

    public ItemsUsedLeaderboard() {
        super("items_used", "Items Used", "How many items/blocks a player has used.");
    }

    @Override
    protected String getStatsCategory() {
        return "minecraft:used";
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
        return "";
    }

    @Override
    protected String formatValue(LeaderboardEntry entry) {
        return "Items: " + NumberFormat.getIntegerInstance().format(entry.value());
    }
}
