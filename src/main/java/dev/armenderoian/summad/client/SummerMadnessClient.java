package dev.armenderoian.summad.client;

import dev.armenderoian.summad.custom.client.particle.DeathResetParticle;
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
    }
}
