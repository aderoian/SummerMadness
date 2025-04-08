package dev.armenderoian.summad.feature.death;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DeathFeature extends AbstractFeature {

    public static byte USE_DEATH_RESET_ITEM_STATUS = 66;

    public ScoreboardObjective deathScoreboard;

    public DeathFeature(String name) {
        super(name);
    }

    @Override
    public void registerFeature() throws Exception {
        deathScoreboard = getOrCreateScoreboard();
    }

    public boolean reduceDeathCount(ServerPlayerEntity player) {
        if (deathScoreboard == null) {
            return false;
        }

        var currentDeaths = deathScoreboard.getScoreboard().getOrCreateScore(player, deathScoreboard);
        if (currentDeaths.getScore() > 0) {
            currentDeaths.incrementScore(-1);
            return true;
        }
        return false;
    }

    private ScoreboardObjective getOrCreateScoreboard() {
        var server = SummerMadness.SERVER;
        if (server == null) {
            throw new IllegalStateException("Server is not initialized");
        }

        var scoreboard = server.getScoreboard();

        var objective = scoreboard.getNullableObjective("deaths");
        if (objective == null) {
            objective = scoreboard.addObjective(
                    "deaths",
                    ScoreboardCriterion.DEATH_COUNT,
                    Text.translatable("scoreboard.summermadness.deaths.name"),
                    ScoreboardCriterion.RenderType.INTEGER,
                    true,
                    null
            );

            scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.LIST, objective);
        }

        return objective;
    }

    public ScoreboardObjective getDeathObjective() {
        return deathScoreboard;
    }
}
