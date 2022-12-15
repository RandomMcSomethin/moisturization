package io.github.randommcsomethin.moisturization.util;

import io.github.randommcsomethin.moisturization.compat.CopperPipesCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.Iterator;

import static io.github.randommcsomethin.moisturization.Moisturization.CONFIG;

public class FarmlandManager {
    public static boolean checkForWater(WorldView world, BlockPos pos) {
        // Water
        int water = CONFIG.waterRange;
        Boolean hasWater = false;

        Iterator var3 = BlockPos.iterate(pos.add(-water, 0, -water), pos.add(water, 1, water)).iterator();
        BlockPos blockPos = new BlockPos(pos.add(-water, 0, -water));

        // Natural water range
        do {
            if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                hasWater = true;
            }
            blockPos = (BlockPos) var3.next();
        } while (var3.hasNext());

        // Simple Copper Pipes compatibility:
        if (FabricLoader.getInstance().isModLoaded("copper_pipe")) {
            if (CONFIG.leakingPipeRadius > 0 &&
                    CopperPipesCompat.moistenFarmlandUnderPipes(world, pos, CONFIG.leakingPipeRadius)) {
                hasWater = true;
            }
        }
        return hasWater;
    }
}
