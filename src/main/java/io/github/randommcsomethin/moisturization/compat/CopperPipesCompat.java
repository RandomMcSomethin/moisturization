package io.github.randommcsomethin.moisturization.compat;

import net.lunade.copper.blocks.CopperPipe;
import net.lunade.copper.blocks.CopperPipeProperties;
import net.lunade.copper.leaking_pipes.LeakingPipeManager;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CopperPipesCompat {
    public static BlockState getConnectedPipe(World world, BlockPos pos) {
        for (Direction d : Direction.values()) {
            BlockState b = world.getBlockState(pos.offset(d, 1));
            if (b.getBlock() instanceof CopperPipe && b.get(Properties.FACING) == d.getOpposite()) {
                return b;
            }
        }
        return null;
    }

    public static boolean moistenFarmlandUnderPipes(WorldView world, BlockPos pos, int water) {
        return (LeakingPipeManager.isWaterPipeNearbyBlockGetter(world, pos, water));
    }
}
