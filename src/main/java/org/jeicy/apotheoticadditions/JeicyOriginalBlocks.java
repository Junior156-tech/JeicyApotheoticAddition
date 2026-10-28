package org.jeicy.apotheoticadditions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Registers the twenty-one block IDs present in the original 2.2.2 JAR.
 * Registry skeleton only. Special enchantment shelf behavior, lighting/state properties
 * and exact Minecraft-world behavior still require porting before release.
 * No KubeJS or AO Port dependency.
 */
public final class JeicyOriginalBlocks {
    public static final String MOD_ID = "apotheotic_additions";
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    private static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredBlock<Block> SACRIFICIAL_SHELF = BLOCKS.registerBlock("sacrificial_shelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> WAVEBINDERS_SHELF = BLOCKS.registerBlock("wavebinders_shelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> VOID_SHELF = BLOCKS.registerBlock("void_shelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> TERRA_SHELF = BLOCKS.registerBlock("terra_shelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> SCRAP_BRICK = BLOCKS.registerBlock("scrap_brick", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    public static final DeferredBlock<RotatedPillarBlock> SCRAP_PILLAR = BLOCKS.registerBlock("scrap_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    public static final DeferredBlock<Block> SCRAP_ROUGH = BLOCKS.registerBlock("scrap_rough", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    public static final DeferredBlock<Block> SCRAP_CARVED = BLOCKS.registerBlock("scrap_carved", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    public static final DeferredBlock<SlabBlock> SCRAP_BRICK_SLAB = BLOCKS.registerBlock("scrap_brick_slab", SlabBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    public static final DeferredBlock<StairBlock> SCRAP_BRICK_STAIRS = BLOCKS.registerBlock("scrap_brick_stairs", props -> new StairBlock(Blocks.STONE_BRICKS.defaultBlockState(), props), BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    public static final DeferredBlock<GlassBlock> LUMINOUS_GLASS = BLOCKS.registerBlock("luminous_glass", GlassBlock::new, BlockBehaviour.Properties.of().strength(0.3F).noOcclusion().lightLevel(state -> 15));
    public static final DeferredBlock<IronBarsBlock> LUMINOUS_GLASS_PANE = BLOCKS.registerBlock("luminous_glass_pane", IronBarsBlock::new, BlockBehaviour.Properties.of().strength(0.3F).noOcclusion().lightLevel(state -> 15));
    public static final DeferredBlock<Block> TIMEWORN_FABRIC = BLOCKS.registerBlock("timeworn_fabric", Block::new, BlockBehaviour.Properties.of().strength(1.0F));
    public static final DeferredBlock<Block> TIMEWORN_FANCY = BLOCKS.registerBlock("timeworn_fancy", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    public static final DeferredBlock<Block> INFUSED_SKYSHELF = BLOCKS.registerBlock("infused_skyshelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> GILDED_AEROGEL_SKYSHELF = BLOCKS.registerBlock("gilded_aerogel_skyshelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> HOLLOWED_BARREL_SHELF = BLOCKS.registerBlock("hollowed_barrel_shelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> NUCLEAR_BARREL_SHELF = BLOCKS.registerBlock("nuclear_barrel_shelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> THORNSHELF = BLOCKS.registerBlock("thornshelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> INFUSED_THORNSHELF = BLOCKS.registerBlock("infused_thornshelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));
    public static final DeferredBlock<Block> OCCULT_THORNSHELF = BLOCKS.registerBlock("occult_thornshelf", Block::new, BlockBehaviour.Properties.of().strength(1.5F));

    static {
        BLOCK_ITEMS.registerSimpleBlockItem("sacrificial_shelf", SACRIFICIAL_SHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("wavebinders_shelf", WAVEBINDERS_SHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("void_shelf", VOID_SHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("terra_shelf", TERRA_SHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("scrap_brick", SCRAP_BRICK);
        BLOCK_ITEMS.registerSimpleBlockItem("scrap_pillar", SCRAP_PILLAR);
        BLOCK_ITEMS.registerSimpleBlockItem("scrap_rough", SCRAP_ROUGH);
        BLOCK_ITEMS.registerSimpleBlockItem("scrap_carved", SCRAP_CARVED);
        BLOCK_ITEMS.registerSimpleBlockItem("scrap_brick_slab", SCRAP_BRICK_SLAB);
        BLOCK_ITEMS.registerSimpleBlockItem("scrap_brick_stairs", SCRAP_BRICK_STAIRS);
        BLOCK_ITEMS.registerSimpleBlockItem("luminous_glass", LUMINOUS_GLASS);
        BLOCK_ITEMS.registerSimpleBlockItem("luminous_glass_pane", LUMINOUS_GLASS_PANE);
        BLOCK_ITEMS.registerSimpleBlockItem("timeworn_fabric", TIMEWORN_FABRIC);
        BLOCK_ITEMS.registerSimpleBlockItem("timeworn_fancy", TIMEWORN_FANCY);
        BLOCK_ITEMS.registerSimpleBlockItem("infused_skyshelf", INFUSED_SKYSHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("gilded_aerogel_skyshelf", GILDED_AEROGEL_SKYSHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("hollowed_barrel_shelf", HOLLOWED_BARREL_SHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("nuclear_barrel_shelf", NUCLEAR_BARREL_SHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("thornshelf", THORNSHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("infused_thornshelf", INFUSED_THORNSHELF);
        BLOCK_ITEMS.registerSimpleBlockItem("occult_thornshelf", OCCULT_THORNSHELF);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
    }

    private JeicyOriginalBlocks() {}
}
