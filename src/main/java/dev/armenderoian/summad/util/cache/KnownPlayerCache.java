package dev.armenderoian.summad.util.cache;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import dev.armenderoian.summad.SummerMadness;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KnownPlayerCache {

    private static final Gson gson = new Gson();

    private static final ConcurrentMap<UUID, KnownPlayer> knownPlayers = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, KnownPlayer> knownPlayersByName = new ConcurrentHashMap<>();

    public static void initCache() {
        var path = SummerMadness.SERVER.getPath("usercache.json");
        try (var in = Files.newBufferedReader(path)) {
            JsonArray jsonArray = gson.fromJson(in, JsonArray.class);
            for (var jsonElement : jsonArray) {
                var jsonObject = jsonElement.getAsJsonObject();
                var uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
                var name = jsonObject.get("name").getAsString();
                var knownPlayer = new KnownPlayer(uuid, name);
                knownPlayers.put(uuid, knownPlayer);
                knownPlayersByName.put(name.toLowerCase(), knownPlayer);
            }
        } catch (Exception e) {
            SummerMadness.LOGGER.error("Failed to load known players from cache", e);
        }

        ServerPlayConnectionEvents.JOIN.register(KnownPlayerCache::handleJoin);
    }

    public static @Nullable KnownPlayer getKnownPlayer(UUID uuid) {
        return knownPlayers.get(uuid);
    }

    public static @Nullable KnownPlayer getKnownPlayer(String name) {
        return knownPlayersByName.get(name.toLowerCase());
    }

    public static List<KnownPlayer> getKnownPlayers() {
        return knownPlayers.values().stream().toList();
    }

    private static void handleJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        var player = serverPlayNetworkHandler.getPlayer();
        var uuid = player.getUuid();
        var name = player.getGameProfile().getName();

        if (!knownPlayers.containsKey(uuid)) {
            var knownPlayer = new KnownPlayer(uuid, name);
            knownPlayers.put(uuid, knownPlayer);
            knownPlayersByName.put(name.toLowerCase(), knownPlayer);
        }
    }

    public record KnownPlayer(UUID uuid, String name) {
    }
}
