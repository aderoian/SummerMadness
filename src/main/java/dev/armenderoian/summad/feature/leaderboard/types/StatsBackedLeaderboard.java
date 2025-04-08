package dev.armenderoian.summad.feature.leaderboard.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.leaderboard.LeaderboardUpdater;
import dev.armenderoian.summad.util.cache.DataCache;
import dev.armenderoian.summad.util.cache.GenericDataCache;
import dev.armenderoian.summad.util.cache.KnownPlayerCache;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public abstract class StatsBackedLeaderboard extends AbstractLeaderboard<JsonObject> {

    public StatsBackedLeaderboard(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    protected int loadValueForPlayer(UUID uuid, DataCache<UUID, JsonObject> cache) {
        return cache.contains(uuid) ? loadValueFromStats(cache.get(uuid)) : 0;
    }

    protected int loadValueFromStats(JsonObject statsData) {
        var catName = getStatsCategory();
        var entryName = getStatsName();
        if (statsData.has(catName)) {
            var statsCategory = statsData.get(catName);
            if (!statsCategory.isJsonObject()) return 0;

            var statsCategoryObject = statsCategory.getAsJsonObject();
            if (!isSummedValue()) {
                return statsCategoryObject.has(entryName) ? statsCategoryObject.get(entryName).getAsInt() : 0;
            } else {
                var total = 0;
                for (var entry : statsCategory.getAsJsonObject().entrySet()) {
                    if (entry.getKey().startsWith(entryName)) {
                        total += entry.getValue().getAsInt();
                    }
                }
                return total;
            }
        }
        return 0;
    }

    protected abstract String getStatsCategory();
    protected abstract String getStatsName();
    protected abstract boolean isSummedValue();

    public static DataCache<UUID, JsonObject> createStatsCache() {
        var cache = new GenericDataCache<UUID, JsonObject>();
        KnownPlayerCache.getKnownPlayers().forEach(player -> {
            var path = getStatsFile(player.uuid());
            try (var reader = Files.newBufferedReader(path)) {
                var statsData = SummerMadness.GSON.fromJson(reader, JsonObject.class);
                if (statsData != null && statsData.has("stats")) {
                    cache.add(player.uuid(), statsData.getAsJsonObject("stats"));
                } else {
                    throw new JsonParseException("Unexpected JSON format, missing 'stats' object");
                }
            } catch (IOException ignored) {
            } catch (JsonParseException e) {
                SummerMadness.LOGGER.warn("JSON error when trying to parse stats file for player: {}: {}", player.uuid(), e.getMessage());
            }
        });

        return cache;
    }

    public static Path getStatsFile(UUID uuid) {
        return SummerMadness.SERVER.getSavePath(WorldSavePath.STATS).resolve(uuid.toString() + ".json");
    }

    public static class StatsBackedLeaderboardUpdater extends LeaderboardUpdater<JsonObject> {

        @Override
        protected DataCache<UUID, JsonObject> createCache() {
            return createStatsCache();
        }
    }
}
