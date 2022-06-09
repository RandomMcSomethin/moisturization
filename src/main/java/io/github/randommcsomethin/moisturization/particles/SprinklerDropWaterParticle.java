package io.github.randommcsomethin.moisturization.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SprinklerDropWaterParticle extends SpriteBillboardParticle {
    protected SprinklerDropWaterParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider p) {
        super(clientWorld, d, e, f, g, h, i);
        this.setSprite(p);
        this.collidesWithWorld = true;
        this.gravityStrength = 0.7F;

        this.velocityX *= 5.5F;
        this.velocityY *= 1.2F;
        this.velocityZ *= 5.5F;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public record DefaultFactory(
            SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new SprinklerDropWaterParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.provider);
        }
    }
}
