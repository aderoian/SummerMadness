package dev.armenderoian.summad.util;

import dev.armenderoian.summad.SummerMadness;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ScoreboardUtils {

    public static ScoreboardObjective getOrCreateScoreboardObjective(ScoreboardObjectiveBuilder ob) {
        var server = SummerMadness.SERVER;
        if (server == null) {
            throw new IllegalStateException("Server is not initialized");
        }

        var scoreboard = server.getScoreboard();

        var objective = scoreboard.getNullableObjective(ob.name);
        if (objective == null) {
            objective = ob.build(scoreboard);
        }

        return objective;
    }

    public static class ScoreboardObjectiveBuilder {
        public String name;
        public ScoreboardCriterion criterion;
        public Text displayName;
        public ScoreboardCriterion.RenderType renderType;
        public boolean displayAutoUpdate;
        public @Nullable NumberFormat format;
        public @Nullable ScoreboardDisplaySlot displaySlot;

        public ScoreboardObjectiveBuilder(String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType, boolean displayAutoUpdate, @Nullable NumberFormat format, @Nullable ScoreboardDisplaySlot displaySlot) {
            this.name = name;
            this.criterion = criterion;
            this.displayName = displayName;
            this.renderType = renderType;
            this.displayAutoUpdate = displayAutoUpdate;
            this.format = format;
            this.displaySlot = displaySlot;
        }

        public ScoreboardObjectiveBuilder() {}

        public ScoreboardObjectiveBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ScoreboardObjectiveBuilder setCriterion(ScoreboardCriterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public ScoreboardObjectiveBuilder setDisplayName(Text displayName) {
            this.displayName = displayName;
            return this;
        }

        public ScoreboardObjectiveBuilder setRenderType(ScoreboardCriterion.RenderType renderType) {
            this.renderType = renderType;
            return this;
        }

        public ScoreboardObjectiveBuilder setDisplayAutoUpdate(boolean displayAutoUpdate) {
            this.displayAutoUpdate = displayAutoUpdate;
            return this;
        }

        public ScoreboardObjectiveBuilder setFormat(@Nullable NumberFormat format) {
            this.format = format;
            return this;
        }

        public ScoreboardObjectiveBuilder setDisplaySlot(@Nullable ScoreboardDisplaySlot displaySlot) {
            this.displaySlot = displaySlot;
            return this;
        }

        public ScoreboardObjective build(ServerScoreboard scoreboard) {
            var ob = scoreboard.addObjective(name, criterion, displayName, renderType, displayAutoUpdate, format);
            if (displaySlot != null)
                scoreboard.setObjectiveSlot(displaySlot, ob);
            return ob;
        }
    }
}
