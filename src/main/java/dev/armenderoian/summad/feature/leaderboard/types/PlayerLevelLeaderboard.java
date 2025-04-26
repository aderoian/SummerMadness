package dev.armenderoian.summad.feature.leaderboard.types;

import dev.armenderoian.summad.util.ScoreboardUtils;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

import java.text.NumberFormat;

public class PlayerLevelLeaderboard extends ObjectiveBackedLeaderboard {
    public PlayerLevelLeaderboard() {
        super("player_level", "Player Level", "The players with the highest xp levels.");
    }

    @Override
    public ScoreboardObjective getObjective() {
        return ScoreboardUtils.getOrCreateScoreboardObjective(new ScoreboardUtils.ScoreboardObjectiveBuilder()
                .setName("player_level")
                .setCriterion(ScoreboardCriterion.LEVEL)
                .setDisplayName(Text.of("Player Level"))
                .setRenderType(ScoreboardCriterion.RenderType.INTEGER)
                .setDisplayAutoUpdate(true)
        );
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
        return "Level: " + NumberFormat.getIntegerInstance().format(entry.value());
    }
}
