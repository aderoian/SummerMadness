package dev.armenderoian.summad.feature.leaderboard;

import dev.armenderoian.summad.util.cache.DataCache;
import dev.armenderoian.summad.util.cache.KnownPlayerCache;
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

    record LeaderboardEntry(KnownPlayerCache.KnownPlayer player, int value) {
    }
}
