package org.jeicy.apotheoticadditions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;

/**
 * Original ID bridge for JEICY port. Source snapshot; not yet a complete playable mod.
 * Special behaviors and all custom blocks still require proper NeoForge ports.
 */
@Mod("apotheotic_additions")
public final class JeicyApotheoticAddition {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("apotheotic_additions");

    static {
        ITEMS.registerSimpleItem("artifact_material", new Item.Properties());
        ITEMS.registerSimpleItem("ancient_material", new Item.Properties());
        ITEMS.registerSimpleItem("heirloom_material", new Item.Properties());
        ITEMS.registerSimpleItem("esoteric_material", new Item.Properties());
        ITEMS.registerSimpleItem("gemboundsigil", new Item.Properties());
        ITEMS.registerSimpleItem("apotheotic_coin", new Item.Properties());
        ITEMS.registerSimpleItem("infused_coin", new Item.Properties());
        ITEMS.registerSimpleItem("ascended_coin", new Item.Properties());
        ITEMS.registerSimpleItem("commonapple", new Item.Properties());
        ITEMS.registerSimpleItem("uncommonapple", new Item.Properties());
        ITEMS.registerSimpleItem("rareapple", new Item.Properties());
        ITEMS.registerSimpleItem("epicapple", new Item.Properties());
        ITEMS.registerSimpleItem("mythicapple", new Item.Properties());
        ITEMS.registerSimpleItem("mythicalband", new Item.Properties());
    }

    public JeicyApotheoticAddition(IEventBus modBus) {
        // Register custom bonus codec before Apotheosis reloads datapacks.
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_attribute"),
            JeicyTieredAttributeBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_enchantment"),
            JeicyTieredEnchantmentBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_multi_attribute"),
            JeicyTieredMultiAttributeBonus.CODEC);
        ITEMS.register(modBus);
        JeicyOriginalBlocks.register(modBus);
        JeicyGemComponents.register(modBus);
        NeoForge.EVENT_BUS.addListener(JeicyRuntimeGemVerifier::onServerStarted);
    }
}
