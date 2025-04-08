package dev.armenderoian.summad.feature.leaderboard.types;

import dev.armenderoian.summad.feature.leaderboard.Leaderboard;
import dev.armenderoian.summad.util.cache.GenericDataCache;
import dev.armenderoian.summad.util.cache.KnownPlayerCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
import java.util.UUID;

public abstract class AbstractLeaderboard<T> implements Leaderboard<T> {

    protected String id, name, description;
    protected LeaderboardEntry[] entries;

    public AbstractLeaderboard(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public LeaderboardEntry[] getEntries() {
        return entries;
    }

    @Override
    public void updateLeaderboard(GenericDataCache<UUID, T> cache) {
        entries = cache.getCache().keySet().stream()
                .map(t -> new LeaderboardEntry(KnownPlayerCache.getKnownPlayer(t), loadValueForPlayer(t, cache)))
                .sorted(this::compare)
                .toArray(LeaderboardEntry[]::new);
    }

    @Override
    public String toGameMessage(int lines) {
        var message = new StringBuilder(); // TODO: Come back and format this better
        message.append("===== ").append(name).append(" =====\n");
        message.append(description).append("\n");
        for (int i = 0; i < Math.min(lines, entries.length); i++) {
            var entry = entries[i];
            message.append(formatLine(entry, i + 1)).append("\n");
        }
        message.append("===== ").append(name).append(" =====");

        return message.toString();
    }

    @Override
    public MessageEmbed toDiscordMessage(int lines) {
        var embed = new EmbedBuilder()
                .setTitle(name)
                .setDescription(description)
                .setColor(Color.CYAN)
                .setFooter("Last updated")
                .setTimestamp(Instant.now());

        for (int i = 0; i < Math.min(lines, entries.length); i++) {
            var entry = entries[i];
            embed.addField(entry.player().name(), formatValue(entry), true);
        }

        return embed.build();
    }

    protected abstract int loadValueForPlayer(UUID uuid, GenericDataCache<UUID, T> cache);

    protected abstract int compare(LeaderboardEntry entry1, LeaderboardEntry entry2);

    protected abstract String formatLine(LeaderboardEntry entry, int lineNumber);
    protected abstract String formatValue(LeaderboardEntry entry);
}
