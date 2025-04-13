package dev.armenderoian.summad.feature.leaderboard;

import dev.armenderoian.summad.util.io.cache.DataCache;
import dev.armenderoian.summad.util.io.cache.KnownPlayerCache;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.UUID;

public interface Leaderboard<T> {

    String getId();

    String getName();

    String getDescription();

    LeaderboardEntry[] getEntries();

    void setEntries(LeaderboardEntry[] entries);

    void updateLeaderboard(DataCache<UUID, T> cache);

    String toGameMessage(int lines);

    MessageEmbed toDiscordMessage(int lines);

    MessageEmbed toDiscordMessage(int lines, UUID show);

    record LeaderboardEntry(KnownPlayerCache.KnownPlayer player, int value) {
    }
}
