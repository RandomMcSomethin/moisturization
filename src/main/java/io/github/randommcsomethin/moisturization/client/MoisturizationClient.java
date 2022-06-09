package io.github.randommcsomethin.moisturization.client;

import io.github.randommcsomethin.moisturization.particles.SprinklerDropWaterParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class MoisturizationClient  implements ClientModInitializer {
    public static DefaultParticleType SPRINKLER_DROP_WATER;

    @Override
    public void onInitializeClient() {
        SPRINKLER_DROP_WATER = Registry.register(Registry.PARTICLE_TYPE, new Identifier("moisturization", "sprinkler_drop_water"), FabricParticleTypes.simple());

        ParticleFactoryRegistry.getInstance().register(SPRINKLER_DROP_WATER, SprinklerDropWaterParticle.DefaultFactory::new);
    }

}
