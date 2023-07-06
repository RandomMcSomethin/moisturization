package io.github.randommcsomethin.moisturization.mixin;

import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeetrootsBlock.class)
public class BeetrootsMixin extends CropBlock {
    private static final BooleanProperty CONSUMED_NUTRIENTS = BooleanProperty.of("consumed_nutrients");

    public BeetrootsMixin(Settings settings) {
        super(settings);
    }

    // ASDNJFLASDFJKLSDJFLASKDGHKLASHFJASASDFKLASJKLFASJKLGSLKASHDFL
    @Inject(at = @At("TAIL"), method = "appendProperties(Lnet/minecraft/state/StateManager$Builder;)V")
    private void append(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(CONSUMED_NUTRIENTS);
    }
}
