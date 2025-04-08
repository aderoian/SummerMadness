package dev.armenderoian.summad.custom.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class DeathResetParticle extends AnimatedParticle {
    DeathResetParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 1.25F);
        this.velocityMultiplier = 0.6F;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.scale *= 0.75F;
        this.maxAge = 60 + this.random.nextInt(12);
        this.setSpriteForAge(spriteProvider);
        if (this.random.nextInt(4) == 0) {
            // Slightly more blue, with some red to make it purple-ish
            this.setColor(
                    0.2F + this.random.nextFloat() * 0.2F, // red: 0.2 - 0.4
                    0.0F + this.random.nextFloat() * 0.1F, // green: 0.0 - 0.1
                    0.4F + this.random.nextFloat() * 0.3F  // blue: 0.4 - 0.7
            );
        } else {
            // Darker and more balanced purple
            this.setColor(
                    0.1F + this.random.nextFloat() * 0.2F, // red: 0.1 - 0.3
                    0.0F + this.random.nextFloat() * 0.1F, // green: 0.0 - 0.1
                    0.3F + this.random.nextFloat() * 0.3F  // blue: 0.3 - 0.6
            );
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new DeathResetParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
