package com.random.moisturization.blocks;

import com.random.moisturization.Moisturization;
import com.random.moisturization.client.MoisturizationClient;
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
import net.minecraft.util.math.random.Random;
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

    public NetheriteSprinklerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(sprinkling, false)
                                                            .with(activated, false)
                                                            .with(facing, Direction.UP));
        this.radius = 4;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        radius = Moisturization.CONFIG.netheriteSprinklerRadius;
        if (!world.isClient) {
            // obstruction check
            boolean obstructed = false;
            if (state.get(facing) == Direction.UP) {
                if (!world.getBlockState(pos.up()).isAir())
                    obstructed = true;
            } else {
                if (!world.getBlockState(pos.down()).isAir())
                    obstructed = true;
            }
            // activation
            if (world.isReceivingRedstonePower(pos) && !obstructed) {
                if (!state.get(activated)) {
                    world.setBlockState(pos, state.with(sprinkling, true).with(activated, true), 2);
                    world.createAndScheduleBlockTick(pos, this, 300);
                }
            } else {
                world.setBlockState(pos, state.with(sprinkling, state.get(sprinkling)).with(activated, false));
            }
            // but not when obstructed
            if (obstructed) {
                world.setBlockState(pos, state.with(sprinkling, false).with(activated, false));
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        SprinklerBlockEntity s = new SprinklerBlockEntity(pos, state);
        s.radius = radius;
        return s;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return SprinklerBlockEntity::tick;
    }
}
