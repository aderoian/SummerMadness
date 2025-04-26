package dev.armenderoian.summad.client;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.client.toast.CombatToast;
import dev.armenderoian.summad.custom.client.particle.DeathResetParticle;
import dev.armenderoian.summad.network.ClientboundPacket;
import dev.armenderoian.summad.registry.ParticleContent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class SummerMadnessClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerParticles();
    }

    void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ParticleContent.DEATH_RESET_ITEM, DeathResetParticle.Factory::new);

        SummerMadness.NETWORK.registerClientbound(ClientboundPacket.ClientboundCombatToast.class, (message, access) -> {
            access.runtime().getToastManager().add(CombatToast.create(message.inCombat(), 2500L));
        });
    }
}
