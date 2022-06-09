package com.random.moisturization.blocks;

/*
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NetheriteSprinklerBlock extends SprinklerBlock {

    public static final BooleanProperty sprinkling = BooleanProperty.of("sprinkling");
    public static final BooleanProperty activated = BooleanProperty.of("activated");
    public static final DirectionProperty facing = DirectionProperty.of("facing", Direction.Type.VERTICAL);
    public static int radius;

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(sprinkling).add(activated).add(facing);
    }

    public NetheriteSprinklerBlock(Settings settings, int radius) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(sprinkling, false)
                                                            .with(activated, false)
                                                            .with(facing, Direction.UP));
        this.radius = radius;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        NetheriteSprinklerBlockEntity s = new NetheriteSprinklerBlockEntity(pos, state);
        s.radius = radius;
        return s;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return SprinklerBlockEntity::tick;
    }
}
*/
