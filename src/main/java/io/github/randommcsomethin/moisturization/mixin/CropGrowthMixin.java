package io.github.randommcsomethin.moisturization.mixin;

import io.github.randommcsomethin.moisturization.Moisturization;
import io.github.randommcsomethin.moisturization.blocks.SoilBlock;
import io.github.randommcsomethin.moisturization.util.GrowthPattern;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static io.github.randommcsomethin.moisturization.Moisturization.CONFIG;

import net.minecraft.util.math.random.Random;

import java.util.List;

@Mixin(CropBlock.class)
public abstract class CropGrowthMixin extends Block {

	@Final
	@Shadow public static final int MAX_AGE = 7;
	@Shadow public abstract int getMaxAge();

	private static final BooleanProperty CONSUMED_NUTRIENTS = BooleanProperty.of("consumed_nutrients");

	public CropGrowthMixin(Settings settings) {
		super(settings);
	}

	// for nutrition stuff
	@Inject(at = @At("TAIL"), method = "<init>")
	private void addData(Settings settings, CallbackInfo ci) {
		this.setDefaultState(this.stateManager.getDefaultState().with(CONSUMED_NUTRIENTS, false));
	}

	@Inject(at = @At("TAIL"), method = "appendProperties(Lnet/minecraft/state/StateManager$Builder;)V")
	private void append(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(CONSUMED_NUTRIENTS);
	}

	@Inject(at = @At("HEAD"), method = "grow(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", cancellable = true)
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (CONFIG.fertilization) {
			fertilizeFarmland(world, pos.down());
			ci.cancel();
		}
	}

	private void fertilizeFarmland(ServerWorld world, BlockPos pos) {
		var n = world.getBlockState(pos).get(SoilBlock.NITROGEN);
		var p = world.getBlockState(pos).get(SoilBlock.PHOSPHORUS);
		// does not fertilize if value is too high
		if (Math.min(5, n + 1) == n &&
			Math.min(5, p + 2) == p) {
				world.setBlockState(pos, world.getBlockState(pos)
					.with(SoilBlock.NITROGEN, 9));
				return;
		}
		world.setBlockState(pos, world.getBlockState(pos)
				.with(SoilBlock.NITROGEN, Math.min(5, n + 1))
				.with(SoilBlock.PHOSPHORUS, Math.min(5, p + 2)));
	}

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
		boolean malnourished = false;
		// check for nutrients
		if (CONFIG.fertilization) {
			Identifier gpId = new Identifier(
					Registries.BLOCK.getId(state.getBlock()).getNamespace(),
					"growth_patterns/" + Registries.BLOCK.getId(state.getBlock()).getPath() + ".json"
					);
			GrowthPattern gp = null;
			for (GrowthPattern g : Moisturization.growthPatterns) {
				if (g.ID.equals(gpId)) {
					gp = g;
					break;
				}
			}
			BlockState soil = world.getBlockState(pos.down());
			// has a growth pattern file
			if (gp != null) {
				// if insufficient, it won't grow (well)
				if (soil.get(SoilBlock.NITROGEN) < gp.n) {
					malnourished = true;
				} else if (soil.get(SoilBlock.PHOSPHORUS) < gp.p) {
					malnourished = true;
				} else if (soil.get(SoilBlock.POTASSIUM) < gp.k) {
					malnourished = true;
				}
			}
			// consume nutrients when growing
			if (state.get(CropBlock.AGE) == 2) {
				if (!state.get(CONSUMED_NUTRIENTS)) {
					var n = soil.get(SoilBlock.NITROGEN);
					var p = soil.get(SoilBlock.PHOSPHORUS);
					var k = soil.get(SoilBlock.POTASSIUM);
					world.setBlockState(pos.down(), soil
							.with(SoilBlock.NITROGEN, Math.max(0, Math.min(5, n - gp.n)))
							.with(SoilBlock.PHOSPHORUS, Math.max(0, Math.min(5, p + gp.p)))
							.with(SoilBlock.POTASSIUM, Math.max(0, Math.min(5, k + gp.k))));
					world.setBlockState(pos, state.with(CONSUMED_NUTRIENTS, true));
				}
			}
		} else {
			malnourished = true;
		}

		// Reduce crop growth
		if (malnourished && world.getRandom().nextDouble() > 1.0 / (float) CONFIG.growthReductor) {
			ci.cancel();
		}
	}

	/// TODO: rewrite this lol
	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		if (stack.getNbt() == null) return;
		tooltip.add(Text.of(
				"§r§c" + stack.getNbt().getCompound("BlockStateTag").getInt("n") + "§7/" +
				"§e" + stack.getNbt().getCompound("BlockStateTag").getInt("p") + "§7/" +
				"§b" + stack.getNbt().getCompound("BlockStateTag").getInt("k")
		));
	}
}
