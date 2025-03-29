package dev.armenderoian.summod.feature.death;

import dev.armenderoian.summod.SummerModded;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public class DeathFeature {

    public static ScoreboardObjective DEATH_SCOREBOARD;

    public static void registerDeathFeature() {
        DEATH_SCOREBOARD = getOrCreateScoreboard();
    }

    private static ScoreboardObjective getOrCreateScoreboard() {
        var server = SummerModded.SERVER;
        if (server == null) {
            throw new IllegalStateException("Server is not initialized");
        }

        var scoreboard = server.getScoreboard();

        var objective = scoreboard.getNullableObjective("deaths");
        if (objective == null) {
            objective = scoreboard.addObjective(
                    "deaths",
                    ScoreboardCriterion.DEATH_COUNT,
                    Text.translatable("scoreboard.summermodded.deaths.name"),
                    ScoreboardCriterion.RenderType.INTEGER,
                    true,
                    null
            );

            scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.LIST, objective);
        }

        return objective;
    }
}
