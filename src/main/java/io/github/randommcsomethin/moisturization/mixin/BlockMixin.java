package io.github.randommcsomethin.moisturization.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at = @At("HEAD"), method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/BlockView;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", cancellable = true)
    public void appendText(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options, CallbackInfo ci) {

    }
}
