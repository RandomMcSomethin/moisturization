package io.github.randommcsomethin.moisturization.mixin;

import io.github.randommcsomethin.moisturization.Moisturization;
import io.github.randommcsomethin.moisturization.blocks.SprinklerBlock;
import io.github.randommcsomethin.moisturization.compat.CopperPipesCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.lunade.copper.leaking_pipes.LeakingPipeManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import net.minecraft.util.math.random.Random;

import static io.github.randommcsomethin.moisturization.Moisturization.CONFIG;

@Mixin(value = FarmlandBlock.class, priority = 500)
public class FarmlandSaturationMixin {

    @Inject(at = @At("HEAD"), method = "isWaterNearby(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private static void isWaterNearby(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // Water
        int water = CONFIG.waterRange;

        // If set to vanilla, do nothing
        if (water == 4) {

        } else {
            Iterator var3 = BlockPos.iterate(pos.add(-water, 0, -water), pos.add(water, 1, water)).iterator();
            BlockPos blockPos = new BlockPos(pos.add(-water, 0, -water));
            Boolean hasWater = false;

            // Natural water range
            do {
                if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                    hasWater = true;
                    cir.setReturnValue(true);
                }
                blockPos = (BlockPos) var3.next();
            } while (var3.hasNext());

            // Simple Copper Pipes compatibility:
            if (FabricLoader.getInstance().isModLoaded("copper_pipe")) {
                if (CopperPipesCompat.moistenFarmlandUnderPipes(world, pos, water + 2)) {
                    hasWater = true;
                    cir.setReturnValue(true);
                }
            }

            if (!hasWater) cir.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V", cancellable = true)
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (world.getBlockState(pos).get(FarmlandBlock.MOISTURE) > 0) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V", cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        int i = state.get(FarmlandBlock.MOISTURE);
        if (i > 0 && random.nextDouble() > ((double)(CONFIG.farmlandDryingRate))/100) ci.cancel();
    }

}
