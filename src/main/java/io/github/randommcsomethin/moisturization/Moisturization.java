package io.github.randommcsomethin.moisturization;

import io.github.randommcsomethin.moisturization.blocks.NetheriteSprinklerBlock;
import io.github.randommcsomethin.moisturization.blocks.SprinklerBlock;
import io.github.randommcsomethin.moisturization.blocks.SprinklerBlockEntity;
import io.github.randommcsomethin.moisturization.config.MoisturizationConfig;
import io.github.randommcsomethin.moisturization.events.TillBlockCallback;
import io.github.randommcsomethin.moisturization.util.FarmlandManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.world.WorldView;

import java.util.Iterator;

public class Moisturization implements ModInitializer {

    public static final Block SPRINKLER = new SprinklerBlock(FabricBlockSettings.create().mapColor(MapColor.ORANGE).requiresTool().strength(2.0f).sounds(BlockSoundGroup.COPPER));
    public static final Block NETHERITE_SPRINKLER = new NetheriteSprinklerBlock(FabricBlockSettings.create().mapColor(MapColor.DEEPSLATE_GRAY).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE));
    public static BlockEntityType<SprinklerBlockEntity> SPRINKLER_ENTITY;
    //public static BlockEntityType<NetheriteSprinklerBlockEntity> NETHERITE_SPRINKLER_ENTITY;

    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.addBefore(Items.DISPENSER.getDefaultStack(),
                new Block[]{
                        SPRINKLER,
                        NETHERITE_SPRINKLER
                }
        ));
    }

    public static MoisturizationConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = AutoConfig.register(MoisturizationConfig.class, GsonConfigSerializer::new).getConfig();
        Registry.register(Registries.BLOCK, new Identifier("moisturization", "sprinkler"), SPRINKLER);
        Registry.register(Registries.ITEM, new Identifier("moisturization", "sprinkler"), new BlockItem(SPRINKLER, new FabricItemSettings()));
        SPRINKLER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("moisturization", "sprinkler"), FabricBlockEntityTypeBuilder.create(SprinklerBlockEntity::new, SPRINKLER, NETHERITE_SPRINKLER).build(null));

        Registry.register(Registries.BLOCK, new Identifier("moisturization", "netherite_sprinkler"), NETHERITE_SPRINKLER);
        Registry.register(Registries.ITEM, new Identifier("moisturization", "netherite_sprinkler"), new BlockItem(NETHERITE_SPRINKLER, new FabricItemSettings()));

        // tilled blocks start out  m o i s t
        TillBlockCallback.EVENT.register((player, world, hand, pos) ->
        {
            BlockState state = world.getBlockState(pos);
            Block bl = state.getBlock();
            if (bl instanceof FarmlandBlock && FarmlandManager.checkForWater(world, pos)) {
                world.setBlockState(pos, state.with(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE));
            }
            return ActionResult.PASS;
        });
    }
}
