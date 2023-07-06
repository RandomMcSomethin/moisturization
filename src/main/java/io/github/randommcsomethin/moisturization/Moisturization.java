package io.github.randommcsomethin.moisturization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import io.github.randommcsomethin.moisturization.blocks.*;
import io.github.randommcsomethin.moisturization.config.MoisturizationConfig;
import io.github.randommcsomethin.moisturization.events.TillBlockCallback;
import io.github.randommcsomethin.moisturization.items.FertilizerItem;
import io.github.randommcsomethin.moisturization.util.FarmlandManager;
import io.github.randommcsomethin.moisturization.util.GrowthPattern;
import io.github.randommcsomethin.moisturization.util.TillActionManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.fabric.impl.transfer.item.ComposterWrapper;
import net.fabricmc.fabric.mixin.content.registry.HoeItemAccessor;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.WorldView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Moisturization implements ModInitializer {

    // growth patterns
    public static List<GrowthPattern> growthPatterns;
    public static SimpleRegistry<GrowthPattern>GROWTH_PATTERNS = FabricRegistryBuilder
            .createSimple(GrowthPattern.class, new Identifier("moisturization", "growth_patterns"))
            .buildAndRegister();

    //public static final Block COMPOSTER = new MoistComposterBlock(FabricBlockSettings.copyOf(Blocks.COMPOSTER));
    public static final Block SPRINKLER = new SprinklerBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(2.0f).sounds(BlockSoundGroup.COPPER));
    public static final Block NETHERITE_SPRINKLER = new NetheriteSprinklerBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE));
    public static BlockEntityType<SprinklerBlockEntity> SPRINKLER_ENTITY;
    //public static BlockEntityType<NetheriteSprinklerBlockEntity> NETHERITE_SPRINKLER_ENTITY;
    public static final Block SOIL = new SoilBlock(FabricBlockSettings.copyOf(Blocks.DIRT));

    public static final Item KELP_MEAL = new FertilizerItem(new FabricItemSettings(), 0, 0, 1);
    public static final Item PLANT_MEAL = new FertilizerItem(new FabricItemSettings(), 1, 0, 0);
    public static final Item COMPOST = new FertilizerItem(new FabricItemSettings(), 3, 1, 0);

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

        growthPatterns = new ArrayList<>();

        // Growth data resource
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            Gson gson = new GsonBuilder().create();
            @Override
            public void reload(ResourceManager manager) {
                growthPatterns.clear();
                for(Identifier id : manager.findResources("growth_patterns", path -> path.getPath().endsWith(".json")).keySet()) {
                    try(InputStream stream = manager.getResource(id).get().getInputStream()) {
                        JsonObject j = (JsonObject) JsonParser.parseReader(new InputStreamReader(stream));
                        GrowthPattern gp = gson.fromJson(j, GrowthPattern.class);
                        gp.ID = id;
                        //Registry.register(GROWTH_PATTERNS, id, gp);
                        System.out.println("Loaded growth pattern " + id);
                        growthPatterns.add(gp);
                    } catch(Exception e) {
                        System.out.println("Error occurred while loading resource json " + id.toString() + e);
                    }

                }
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier("moisturization", "growth_data");
            }
        });

        //Registry.register(Registries.BLOCK, new Identifier("moisturization", "composter"), COMPOSTER);
        //Registry.register(Registries.ITEM, new Identifier("moisturization", "composter"), new BlockItem(COMPOSTER, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier("moisturization", "sprinkler"), SPRINKLER);
        Registry.register(Registries.ITEM, new Identifier("moisturization", "sprinkler"), new BlockItem(SPRINKLER, new FabricItemSettings()));
        SPRINKLER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("moisturization", "sprinkler"), FabricBlockEntityTypeBuilder.create(SprinklerBlockEntity::new, SPRINKLER, NETHERITE_SPRINKLER).build(null));

        Registry.register(Registries.BLOCK, new Identifier("moisturization", "netherite_sprinkler"), NETHERITE_SPRINKLER);
        Registry.register(Registries.ITEM, new Identifier("moisturization", "netherite_sprinkler"), new BlockItem(NETHERITE_SPRINKLER, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier("moisturization", "soil"), SOIL);
        Registry.register(Registries.ITEM, new Identifier("moisturization", "soil"), new BlockItem(SOIL, new FabricItemSettings()));

        Registry.register(Registries.ITEM, new Identifier("moisturization", "kelp_meal"), KELP_MEAL);
        Registry.register(Registries.ITEM, new Identifier("moisturization", "plant_meal"), PLANT_MEAL);
        Registry.register(Registries.ITEM, new Identifier("moisturization", "compost"), COMPOST);

        // farmland turns into soil instead of dirt
        HoeItemAccessor.getTillingActions().put(SOIL, Pair.of(HoeItem::canTillFarmland,
                TillActionManager.createTillActionExtended(Blocks.FARMLAND.getDefaultState())));

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
