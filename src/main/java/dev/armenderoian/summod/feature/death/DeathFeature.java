package dev.armenderoian.summod.feature.death;

import dev.armenderoian.summod.SummerModded;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DeathFeature {

    public static byte USE_DEATH_RESET_ITEM_STATUS = 66;

    public static ScoreboardObjective DEATH_SCOREBOARD;

    public static void registerDeathFeature() {
        DEATH_SCOREBOARD = getOrCreateScoreboard();
    }

    public static boolean reduceDeathCount(ServerPlayerEntity player) {
        if (DEATH_SCOREBOARD == null) {
            return false;
        }

        var currentDeaths = DEATH_SCOREBOARD.getScoreboard().getOrCreateScore(player, DEATH_SCOREBOARD);
        if (currentDeaths.getScore() > 0) {
            currentDeaths.incrementScore(-1);
            return true;
        }
        return false;
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
