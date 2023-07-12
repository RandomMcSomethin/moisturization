package io.github.randommcsomethin.moisturization.blocks;

import io.github.randommcsomethin.moisturization.Moisturization;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SprinklerBlockEntity extends BlockEntity {

    private boolean sprinkling = false;
    public static int radius = 2;

    public SprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(Moisturization.SPRINKLER_ENTITY, pos, state);
    }

    // Serialize the BlockEntity
    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        // Save the current value of the number to the tag
        tag.putBoolean("sprinkling", sprinkling);
    }

    // Deserialize the BlockEntity
    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        sprinkling = tag.getBoolean("sprinkling");
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, T t) {
        if (blockState.get(SprinklerBlock.sprinkling)) {
            if (blockState.isOf(Moisturization.SPRINKLER))
                radius = Moisturization.CONFIG.sprinklerRadius;
            else if (blockState.isOf(Moisturization.NETHERITE_SPRINKLER))
                radius = Moisturization.CONFIG.netheriteSprinklerRadius;
            Random random = world.getRandom();
            if (random.nextDouble() < 0.15) {
                if (!world.isClient) {
                    world.playSound(
                            null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                            pos, // The position of where the sound will come from
                            SoundEvents.WEATHER_RAIN, // The sound that will play, in this case, the sound the anvil plays when it lands.
                            SoundCategory.BLOCKS, // This determines which of the volume sliders affect this sound
                            0.5f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                            1.5f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
                    );
                }
            }
            if (world.isClient) {
                SprinklerBlock.spawnParticles(world, pos, radius);
            }
            for (int i = 0; i < 32; ++i) {
                int j = 1;
                if (blockState.get(SprinklerBlock.facing) == Direction.DOWN) j = 4;
                BlockPos blockPos = pos.add(random.nextInt(1 + 2*radius) - radius, random.nextInt(3) - j, random.nextInt(1 + 2*radius) - radius);
                // moisten farmland
                if (world.getBlockState(blockPos).isOf(Blocks.FARMLAND)) {
                    world.setBlockState(blockPos, Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7), 2);
                }
                // extinguish fires
                if (world.getBlockState(blockPos).isIn(BlockTags.FIRE)) {
                    world.removeBlock(blockPos, false);
                }
            }
            // interactions with entities
            // sets up range
            Box range = new Box(pos.add(-radius, -1, -radius), pos.add(radius, 2, radius));
            if (blockState.get(SprinklerBlock.facing) == Direction.DOWN)
                range = range.offset(0, -3, 0);
            // iterates through entities
            for (Entity e: world.getEntitiesByClass(Entity.class, range, entity -> true)) {
                // hurt water-vulnerable mobs
                if (e instanceof LivingEntity && (((LivingEntity) e).hurtByWater() || e instanceof BeeEntity)) {
                    e.damage(world.getDamageSources().drown(), 1.0F);
                }
                // extinguish entities
                if (e.getFireTicks() > 0) {
                    e.extinguish();
                }
            }
        }
    }
}
