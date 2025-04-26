package dev.armenderoian.summad.feature.death;

import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.util.ScoreboardUtils;
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
        deathScoreboard = ScoreboardUtils.getOrCreateScoreboardObjective(new ScoreboardUtils.ScoreboardObjectiveBuilder()
                .setName("deaths")
                .setCriterion(ScoreboardCriterion.DEATH_COUNT)
                .setDisplayName(Text.translatable("scoreboard.summermadness.deaths.name"))
                .setRenderType(ScoreboardCriterion.RenderType.INTEGER)
                .setDisplayAutoUpdate(true)
                .setDisplaySlot(ScoreboardDisplaySlot.LIST)
        );
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

    public ScoreboardObjective getDeathObjective() {
        return deathScoreboard;
    }
}
