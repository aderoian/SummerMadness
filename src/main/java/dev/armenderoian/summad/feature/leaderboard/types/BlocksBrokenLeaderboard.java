package dev.armenderoian.summad.feature.leaderboard.types;

import java.text.NumberFormat;

public class BlocksBrokenLeaderboard extends StatsBackedLeaderboard {

    public BlocksBrokenLeaderboard() {
        super("blocks_broken", "Blocks Broken", "How many blocks a player has mined.");
    }

    @Override
    protected String getStatsCategory() {
        return "minecraft:mined";
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
        return "Blocks: " + NumberFormat.getIntegerInstance().format(entry.value());
    }
}
