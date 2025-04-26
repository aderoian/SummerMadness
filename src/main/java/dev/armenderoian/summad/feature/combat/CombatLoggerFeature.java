package dev.armenderoian.summad.feature.combat;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.network.ClientboundPacket;
import dev.armenderoian.summad.util.ServerConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatLoggerFeature extends AbstractFeature {

    private static final ConcurrentHashMap<UUID, Instant> combatSessions = new ConcurrentHashMap<>();

    public CombatLoggerFeature(String name) {
        super(name);

        SummerMadness.SCHEDULER.scheduleAtFixedRate(CombatLoggerFeature::update, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void registerFeature() throws Exception {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(CombatLoggerFeature::afterDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(CombatLoggerFeature::afterDeath);
        ServerPlayConnectionEvents.DISCONNECT.register(CombatLoggerFeature::afterDisconnect);
    }

    @Override
    public void onStart(MinecraftServer server) throws Exception {
        SummerMadness.SCHEDULER.scheduleAtFixedRate(CombatLoggerFeature::update, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    private static void afterDisconnect(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        var uuid = serverPlayNetworkHandler.getPlayer().getUuid();
        if (combatSessions.containsKey(uuid)) {
            var lastCombatTime = combatSessions.get(uuid);
            if (Duration.between(lastCombatTime, Instant.now()).abs().compareTo(Duration.ofSeconds(ServerConfig.combatLoggerCooldown)) < 0) {
                serverPlayNetworkHandler.getPlayer().kill();
            }
            combatSessions.remove(serverPlayNetworkHandler.getPlayer().getUuid());
        }
    }

    private static void afterDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity instanceof ServerPlayerEntity player) {
            if (damageSource.getAttacker() instanceof ServerPlayerEntity) {
                if (combatSessions.remove(player.getUuid()) != null) {
                    SummerMadness.NETWORK.serverHandle(player).send(new ClientboundPacket.ClientboundCombatToast(false));
                }
            }
        }
    }

    private static void afterDamage(LivingEntity livingEntity, DamageSource damageSource, float v, float v1, boolean b) {
        if (livingEntity instanceof ServerPlayerEntity player) {
            if (damageSource.getAttacker() instanceof ServerPlayerEntity) {
                if (combatSessions.containsKey(player.getUuid())) {
                    var lastCombatTime = combatSessions.get(player.getUuid());
                    if (Duration.between(lastCombatTime, Instant.now()).abs().compareTo(Duration.ofSeconds(ServerConfig.combatLoggerCooldown)) > 0)
                        SummerMadness.NETWORK.serverHandle(player).send(new ClientboundPacket.ClientboundCombatToast(true));
                    combatSessions.put(player.getUuid(), Instant.now());
                } else {
                    combatSessions.put(player.getUuid(), Instant.now());
                    SummerMadness.NETWORK.serverHandle(player).send(new ClientboundPacket.ClientboundCombatToast(true));
                }
            }
        }
    }

    private static void update() {
        combatSessions.entrySet().stream().filter(entry -> Duration.between(entry.getValue(), Instant.now()).abs().compareTo(Duration.ofSeconds(ServerConfig.combatLoggerCooldown)) > 0).forEach(entry -> {
            combatSessions.remove(entry.getKey());
            var player = SummerMadness.SERVER.getPlayerManager().getPlayer(entry.getKey());
            if (player != null) {
                SummerMadness.NETWORK.serverHandle(player).send(new ClientboundPacket.ClientboundCombatToast(false));
            }
        });
    }
}
