package io.github.randommcsomethin.moisturization.mixin;

import io.github.randommcsomethin.moisturization.Moisturization;
import io.github.randommcsomethin.moisturization.blocks.SprinklerBlock;
import io.github.randommcsomethin.moisturization.compat.CopperPipesCompat;
import io.github.randommcsomethin.moisturization.util.FarmlandManager;
import net.fabricmc.loader.api.FabricLoader;
import net.lunade.copper.leaking_pipes.LeakingPipeManager;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
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
public class FarmlandSaturationMixin extends Block {

    private static final IntProperty NITROGEN = IntProperty.of("n", 0, 5);
    private static final IntProperty PHOSPHORUS = IntProperty.of("p", 0, 5);
    private static final IntProperty POTASSIUM = IntProperty.of("k", 0, 5);

    public FarmlandSaturationMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void addData(AbstractBlock.Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NITROGEN, 3)
                .with(PHOSPHORUS, 3)
                .with(POTASSIUM, 3));
    }

    @Inject(at = @At("TAIL"), method = "appendProperties(Lnet/minecraft/state/StateManager$Builder;)V")
    private void append(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(NITROGEN, PHOSPHORUS, POTASSIUM);
    }

    @Inject(at = @At("HEAD"), method = "isWaterNearby(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private static void isWaterNearby(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.waterRange != 4)
            cir.setReturnValue(FarmlandManager.checkForWater(world, pos));
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

    @Inject(at = @At("HEAD"), method = "setToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", cancellable = true)
    private static void setToDirt(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        world.setBlockState(pos, pushEntitiesUpBeforeBlockChange(state, Moisturization.SOIL.getDefaultState()
                .with(FarmlandBlock.MOISTURE, world.getBlockState(pos).get(FarmlandBlock.MOISTURE))
                .with(NITROGEN, world.getBlockState(pos).get(NITROGEN))
                .with(PHOSPHORUS, world.getBlockState(pos).get(PHOSPHORUS))
                .with(POTASSIUM, world.getBlockState(pos).get(POTASSIUM)), world, pos));
        ci.cancel();
    }

}
