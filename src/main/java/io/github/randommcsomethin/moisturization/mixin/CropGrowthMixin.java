package io.github.randommcsomethin.moisturization.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static io.github.randommcsomethin.moisturization.Moisturization.CONFIG;

import net.minecraft.util.math.random.Random;

@Mixin(CropBlock.class)
public class CropGrowthMixin {

	@Inject(at = @At("HEAD"), method = "applyGrowth(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", cancellable = true)
	public void applyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (CONFIG.boneMealReductor <= 0) {
			ci.cancel();
		}
		if (!(world.getBlockState(pos).getBlock() instanceof SaplingBlock) && world.getRandom().nextDouble() > 1.0/((float)CONFIG.boneMealReductor)) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V", cancellable = true)
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		// Dying crops
		if (CONFIG.cropsDie) {
			// make crops die on dry farmland
			BlockState farmland = world.getBlockState(pos.down());
			if (farmland.getBlock() instanceof FarmlandBlock && farmland.get(FarmlandBlock.MOISTURE) < 1)
				world.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState());
		}
		// Reduce crop growth
		if (world.getRandom().nextDouble() > 1.0/(float)CONFIG.growthReductor) {
			ci.cancel();
		}
	}
}
