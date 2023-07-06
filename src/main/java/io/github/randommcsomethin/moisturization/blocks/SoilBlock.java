package io.github.randommcsomethin.moisturization.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoilBlock extends Block {

    public static final IntProperty MOISTURE = Properties.MOISTURE;
    public static final IntProperty NITROGEN = IntProperty.of("n", 0, 5);
    public static final IntProperty PHOSPHORUS = IntProperty.of("p", 0, 5);
    public static final IntProperty POTASSIUM = IntProperty.of("k", 0, 5);

    public SoilBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(MOISTURE, 0)
                .with(NITROGEN, 0)
                .with(PHOSPHORUS, 0)
                .with(POTASSIUM, 0));
    }


    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MOISTURE, NITROGEN, PHOSPHORUS, POTASSIUM);
    }

    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        if (stack.getNbt() == null) return;
        tooltip.add(Text.of(
                "§r§c" + stack.getNbt().getCompound("BlockStateTag").getInt("n") + "§7/" +
                "§e" + stack.getNbt().getCompound("BlockStateTag").getInt("p") + "§7/" +
                "§b" + stack.getNbt().getCompound("BlockStateTag").getInt("k")
        ));
    }
}
