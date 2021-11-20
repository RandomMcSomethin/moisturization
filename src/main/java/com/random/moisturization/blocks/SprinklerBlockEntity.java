package com.random.moisturization.blocks;

import com.random.moisturization.Moisturization;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class SprinklerBlockEntity extends BlockEntity {

    private boolean sprinkling = false;

    public SprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(Moisturization.SPRINKLER_ENTITY, pos, state);
    }

    // Serialize the BlockEntity
    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        // Save the current value of the number to the tag
        tag.putBoolean("sprinkling", sprinkling);

        return tag;
    }

    // Deserialize the BlockEntity
    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        sprinkling = tag.getBoolean("sprinkling");
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, T t) {
        if (blockState.get(com.random.moisturization.blocks.SprinklerBlock.sprinkling)) {
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
            com.random.moisturization.blocks.SprinklerBlock.spawnParticles(world, pos);
            for (int i = 0; i < 32; ++i) {
                BlockPos blockPos = pos.add(random.nextInt(5) - 2, random.nextInt(2) - 1, random.nextInt(5) - 2);
                if (world.getBlockState(blockPos).isOf(Blocks.FARMLAND)) {
                    world.setBlockState(blockPos, Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7), 2);
                }
            }

            // Todo: interactions with mobs
        }
    }
}
