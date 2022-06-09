package com.random.moisturization.blocks;

import com.random.moisturization.Moisturization;
import com.random.moisturization.client.MoisturizationClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.random.Random;

public class SprinklerBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty sprinkling = BooleanProperty.of("sprinkling");
    public static final BooleanProperty activated = BooleanProperty.of("activated");
    public static final DirectionProperty facing = DirectionProperty.of("facing", Direction.Type.VERTICAL);
    public static int radius;

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(sprinkling).add(activated).add(facing);
    }

    public SprinklerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(sprinkling, false)
                .with(activated, false)
                .with(facing, Direction.UP));
        this.radius = 2;
    }

    public SprinklerBlock(Settings settings, int radius) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(sprinkling, false)
                                                            .with(activated, false)
                                                            .with(facing, Direction.UP));
        this.radius = radius;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(facing, ctx.getVerticalPlayerLookDirection().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.setBlockState(pos, state.with(sprinkling, false).with(activated, false));
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        radius = Moisturization.CONFIG.sprinklerRadius;
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

    protected static void spawnParticles(World world, BlockPos pos) {
        radius = Moisturization.CONFIG.sprinklerRadius;
        double d = 30*radius/2;
        Random random = world.random;
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
        double e = (double)random.nextFloat() - 0.5D;
        double f = (double)random.nextFloat() - 0.5D;
        double g = -1;
        if (world.getBlockState(pos).get(facing) == Direction.UP) g = 1;
        double h = 1;
        if (g == -1) h = 0;
        world.addParticle(MoisturizationClient.SPRINKLER_DROP_WATER, (double)pos.getX() + 0.5D, (double)pos.getY() + h, (double)pos.getZ() + 0.5D, e*d, 60.0D*g, f*d);
        }
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(sprinkling, false), 2);
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
