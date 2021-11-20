package com.random.moisturization.mixin;

import com.random.moisturization.Moisturization;
import com.random.moisturization.blocks.SprinklerBlock;
import com.random.moisturization.config.MoisturizationConfig;
import me.shedaniel.autoconfig.AutoConfig;
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
import java.util.Random;

import static com.random.moisturization.Moisturization.CONFIG;

@Mixin(FarmlandBlock.class)
public class FarmlandSaturationMixin {

    @Inject(at = @At("HEAD"), method = "isWaterNearby(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private static void isWaterNearby(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // Water
        int water = CONFIG.waterRange;

        Iterator var3 = BlockPos.iterate(pos.add(-water, 0, -water), pos.add(water, 1, water)).iterator();
        BlockPos blockPos = new BlockPos(pos.add(-water, 0, -water));
        Boolean hasWater = false;

        do {
            if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                hasWater = true;
                cir.setReturnValue(true);
            }
            blockPos = (BlockPos)var3.next();
        } while(var3.hasNext());


        // Sprinkler
        Iterator var4 = BlockPos.iterate(pos.add(-3, 0, -3), pos.add(3, 1, 3)).iterator();
        BlockPos blockPos2 = new BlockPos(pos.add(-3, 0, -3));

        do {
            if (world.getBlockState(blockPos2) == Moisturization.SPRINKLER.getDefaultState().with(SprinklerBlock.sprinkling, true)) {
                hasWater = true;
                cir.setReturnValue(true);
            }
            blockPos2 = (BlockPos)var4.next();
        } while(var4.hasNext());

        if (!hasWater) cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V", cancellable = true)
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (world.getBlockState(pos).get(FarmlandBlock.MOISTURE) > 0) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V", cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        int i = state.get(FarmlandBlock.MOISTURE);
        if (i > 0 && random.nextDouble() > ((double)(CONFIG.farmlandDryingRate))/100) ci.cancel();
    }

}
