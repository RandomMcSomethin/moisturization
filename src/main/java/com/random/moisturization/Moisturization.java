package com.random.moisturization;

import com.random.moisturization.blocks.SprinklerBlock;
import com.random.moisturization.blocks.SprinklerBlockEntity;
import com.random.moisturization.config.MoisturizationConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Moisturization implements ModInitializer {

    public static final Block SPRINKLER = new SprinklerBlock(FabricBlockSettings.of(Material.METAL).strength(2.0f).requiresTool().breakByTool(FabricToolTags.PICKAXES));
    public static BlockEntityType<SprinklerBlockEntity> SPRINKLER_ENTITY;

    public static MoisturizationConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = AutoConfig.register(MoisturizationConfig.class, GsonConfigSerializer::new).getConfig();
        Registry.register(Registry.BLOCK, new Identifier("moisturization", "sprinkler"), SPRINKLER);
        Registry.register(Registry.ITEM, new Identifier("moisturization", "sprinkler"), new BlockItem(SPRINKLER, new FabricItemSettings().group(ItemGroup.REDSTONE)));
        SPRINKLER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("moisturization", "sprinkler"), FabricBlockEntityTypeBuilder.create(SprinklerBlockEntity::new, SPRINKLER).build(null));
    }
}
