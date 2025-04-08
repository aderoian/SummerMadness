package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ParticleContent {

    public static final SimpleParticleType DEATH_RESET_ITEM = registerParticle("death_reset_item");

    public static SimpleParticleType registerParticle(String name) {
        var particle = FabricParticleTypes.simple();
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(SummerMadness.MOD_ID, name), particle);
    }
}
