package io.github.randommcsomethin.moisturization.mixin;

import io.github.randommcsomethin.moisturization.events.TillBlockCallback;
import net.minecraft.block.CropBlock;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public class HoeMixin {

    @Inject(at = @At("RETURN"), method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable cir) {
        // fires event ONLY if a block is tilled (success)
        if (cir.getReturnValue() != ActionResult.PASS && cir.getReturnValue() != ActionResult.FAIL) {
            ActionResult result = TillBlockCallback.EVENT.invoker().interact(context.getPlayer(), context.getWorld(), context.getHand(), context.getBlockPos());

            if(result == ActionResult.FAIL) {
                cir.cancel();
            }
        }
    }
}
