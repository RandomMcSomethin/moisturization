package io.github.randommcsomethin.moisturization.util;

import io.github.randommcsomethin.moisturization.blocks.SoilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.event.GameEvent;

import java.util.function.Consumer;

public class TillActionManager {

    public static Consumer<ItemUsageContext> createTillActionExtended(BlockState result) {
        return (context) -> {
            var bl = context.getWorld().getBlockState(context.getBlockPos());
            context.getWorld().setBlockState(context.getBlockPos(), result
                            .with(SoilBlock.MOISTURE, bl.get(SoilBlock.MOISTURE))
                            .with(SoilBlock.NITROGEN, bl.get(SoilBlock.NITROGEN))
                            .with(SoilBlock.PHOSPHORUS, bl.get(SoilBlock.PHOSPHORUS))
                            .with(SoilBlock.POTASSIUM, bl.get(SoilBlock.POTASSIUM)),
                    11);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(), GameEvent.Emitter.of(context.getPlayer(), result));
        };
    }
}
