package com.random.moisturization.blocks;

import com.random.moisturization.client.MoisturizationClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SprinklerBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty sprinkling = BooleanProperty.of("sprinkling");
    public static final BooleanProperty activated = BooleanProperty.of("activated");

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(sprinkling);
        stateManager.add(activated);
    }

    public SprinklerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(sprinkling, false));
        setDefaultState(getStateManager().getDefaultState().with(activated, false));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.setBlockState(pos, state.with(sprinkling, false).with(activated, false));
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            if (world.isReceivingRedstonePower(pos) && world.getBlockState(pos.up()).isAir()) {
                if (!state.get(activated)) {
                    world.setBlockState(pos, state.with(sprinkling, true).with(activated, true), 2);
                    world.getBlockTickScheduler().schedule(pos, this, 300);
                }
            } else {
                world.setBlockState(pos, state.with(sprinkling, state.get(sprinkling)).with(activated, false));
            }
            if (!world.getBlockState(pos.up()).isAir()) {
                world.setBlockState(pos, state.with(sprinkling, false).with(activated, false));
            }

        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {

    }

    protected static void spawnParticles(World world, BlockPos pos) {
        double d = 0.5625D;
        Random random = world.random;
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
        double e = (double)random.nextFloat() - 0.5D;
        double f = (double)random.nextFloat() - 0.5D;
        world.addParticle(MoisturizationClient.SPRINKLER_DROP_WATER, (double)pos.getX() + 0.5D, (double)pos.getY() + 1, (double)pos.getZ() + 0.5D, e*30, 60.0D, f*30);
        }

    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(sprinkling, false), 2);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SprinklerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return SprinklerBlockEntity::tick;
    }
}
