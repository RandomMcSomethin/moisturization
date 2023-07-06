package io.github.randommcsomethin.moisturization.mixin;

import io.github.randommcsomethin.moisturization.Moisturization;
import io.github.randommcsomethin.moisturization.blocks.SoilBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BoneMealItem.class)
public class BoneMealMixin extends ItemMixin {

    int nitrogen = 1;
    int phosphorous = 2;
    int potassium = 0;

    @Inject(at = @At("HEAD"), method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // override default behavior if fertilizer is enabled
        if (!Moisturization.CONFIG.fertilization) {

        } else {
            var bl = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
            var target = context.getBlockPos().mutableCopy();
            if (bl instanceof Fertilizable) {
                target.move(Direction.DOWN);
            }
            // checks if target can be fertilized and does that
            var tar = context.getWorld().getBlockState(target);
            if (tar.getProperties().contains(SoilBlock.NITROGEN)) {
                var n = tar.get(SoilBlock.NITROGEN);
                var p = tar.get(SoilBlock.PHOSPHORUS);
                var k = tar.get(SoilBlock.POTASSIUM);
                // does not fertilize if value is too high
                if (Math.min(5, n + this.nitrogen) == n &&
                        Math.min(5, p + this.phosphorous) == p &&
                        Math.min(5, k + this.potassium) == k) {
                    cir.setReturnValue(ActionResult.FAIL);
                } else {
                    context.getWorld().setBlockState(target, tar
                            .with(SoilBlock.NITROGEN, Math.min(5, n + this.nitrogen))
                            .with(SoilBlock.PHOSPHORUS, Math.min(5, p + this.phosphorous))
                            .with(SoilBlock.POTASSIUM, Math.min(5, k + this.potassium)));
                    context.getStack().decrement(1);
                    BoneMealItem.createParticles(context.getWorld(), target.up(), 4);
                    if (!context.getWorld().isClient) {
                        context.getWorld().playSound(
                                null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                                context.getBlockPos(), // The position of where the sound will come from
                                SoundEvents.ITEM_BONE_MEAL_USE, // The sound that will play, in this case, the sound the anvil plays when it lands.
                                SoundCategory.BLOCKS, // This determines which of the volume sliders affect this sound
                                1.0f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                                1.0f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
                        );
                    }
                    cir.setReturnValue(ActionResult.success(context.getWorld().isClient()));
                }
            }
        }
    }

    public void appendText(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (Moisturization.CONFIG.fertilization) {
            tooltip.add(Text.of(
                    "§r§c" + nitrogen + "§7/" +
                    "§e" + phosphorous + "§7/" +
                    "§b" + potassium
            ));
        }
    }
}
