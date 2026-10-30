package org.jeicy.apotheoticadditions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;

/**
 * Original ID bridge for JEICY port. Source snapshot; not yet a complete playable mod.
 * Special behaviors and all custom blocks still require proper NeoForge ports.
 */
@Mod("apotheotic_additions")
public final class JeicyApotheoticAddition {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("apotheotic_additions");
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, "apotheotic_additions");

    public static final DeferredItem<Item> ARTIFACT_MATERIAL =
        ITEMS.registerSimpleItem("artifact_material", new Item.Properties());
    public static final DeferredItem<Item> ANCIENT_MATERIAL =
        ITEMS.registerSimpleItem("ancient_material", new Item.Properties());
    public static final DeferredItem<Item> HEIRLOOM_MATERIAL =
        ITEMS.registerSimpleItem("heirloom_material", new Item.Properties());
    public static final DeferredItem<Item> ESOTERIC_MATERIAL =
        ITEMS.registerSimpleItem("esoteric_material", new Item.Properties());
    public static final DeferredHolder<RecipeSerializer<?>, OriginalRankUpgradeRecipe.Serializer>
        ORIGINAL_RANK_UPGRADE_SERIALIZER = RECIPE_SERIALIZERS.register(
            "original_rank_upgrade", () -> OriginalRankUpgradeRecipe.Serializer.INSTANCE);

    static {
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
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_all_stats"),
            JeicyTieredAllStatsBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_mageslayer"),
            JeicyTieredMageSlayerBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_midas_weapon"),
            JeicyMidasWeaponBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_mob_effect"),
            JeicyTieredMobEffectBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_split_attack_effect"),
            JeicySplitAttackEffectBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_felfire_weapon"),
            JeicyFelfireWeaponBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_candied_weapon"),
            JeicyCandiedWeaponBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_safari_weapon"),
            JeicySafariWeaponBonus.CODEC);
        GemBonus.CODEC.register(ResourceLocation.fromNamespaceAndPath("apotheotic_additions", "original_drop_transform"),
            JeicyTieredDropTransformBonus.CODEC);
        ITEMS.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        JeicyLootCategories.register(modBus);
        JeicyOriginalBlocks.register(modBus);
        JeicyGemComponents.register(modBus);
        NeoForge.EVENT_BUS.addListener(JeicyRuntimeGemVerifier::onServerStarted);
    }
}
