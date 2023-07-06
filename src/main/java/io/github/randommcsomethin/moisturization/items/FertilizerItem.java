package io.github.randommcsomethin.moisturization.items;

import io.github.randommcsomethin.moisturization.blocks.SoilBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FertilizerItem extends Item implements Fertilizer {
    int nitrogen = 0;
    int phosphorous = 0;
    int potassium = 0;

    public FertilizerItem(Settings settings) {
        super(settings);
    }

    public FertilizerItem(Settings settings, int n, int p, int k) {
        super(settings);
        this.nitrogen = n;
        this.phosphorous = p;
        this.potassium = k;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
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
                return ActionResult.PASS;
            }
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
            return ActionResult.success(context.getWorld().isClient());
        }
        // nothing
        return ActionResult.PASS;
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.of(
                "§r§c" + nitrogen + "§7/" +
                "§e" + phosphorous + "§7/" +
                "§b" + potassium
        ));
    }
}
