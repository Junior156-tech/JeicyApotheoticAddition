package org.jeicy.apotheoticadditions;

import java.util.Map;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.affix.ItemAffixes;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.apotheosis.tiers.TieredWeights;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/** Fails the development server if any original rarity was not decoded exactly. */
public final class JeicyRuntimeRarityVerifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MOD_ID = "apotheotic_additions";
    private static final Map<String, Expected> EXPECTED = Map.of(
        "ancient", new Expected(725, 1, 5.0F, 1, 9, 5, 0.75F),
        "artifact", new Expected(750, 5, 8.0F, 0, 3, 4, 0.5F),
        "heirloom", new Expected(800, 25, 5.0F, 0, 2, 1, 0.7F),
        "esoteric", new Expected(850, 0, 0.0F, 0, 10, 5, 0.7F)
    );

    private JeicyRuntimeRarityVerifier() {}

    public static void onServerStarted(ServerStartedEvent event) {
        if (RarityRegistry.getSortedRarities().size() != 9) {
            throw new IllegalStateException("Expected 9 total rarities, got "
                + RarityRegistry.getSortedRarities().size());
        }

        for (Map.Entry<String, Expected> entry : EXPECTED.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, entry.getKey());
            LootRarity rarity = RarityRegistry.INSTANCE.getValue(id);
            Expected expected = entry.getValue();
            if (rarity == null) throw new IllegalStateException("Missing runtime rarity " + id);
            if (rarity.sortIndex() != expected.sortIndex) {
                throw new IllegalStateException(id + " sort index mismatch: " + rarity.sortIndex());
            }
            if (!rarity.getMaterial().equals(material(entry.getKey()))) {
                throw new IllegalStateException(id + " material mismatch: " + rarity.getMaterial());
            }
            if (rarity.rules().size() != 1 || !(rarity.rules().getFirst() instanceof JeicyOriginalRarityLootRule rule)) {
                throw new IllegalStateException(id + " did not decode the original rarity rule");
            }
            if (rule.removedAffixChances().size() != expected.removedRules
                || rule.affixes().size() != expected.affixRules
                || rule.socketChances().size() != expected.socketRules
                || rule.durability().isEmpty()
                || Float.compare(rule.durability().orElseThrow(), expected.durability) != 0) {
                throw new IllegalStateException(id + " rule sequence mismatch: " + rule);
            }
            for (WorldTier tier : WorldTier.values()) {
                TieredWeights.Weight weight = rarity.weights().weights().get(tier);
                if (weight == null || weight.weight() != expected.weight
                    || Float.compare(weight.quality(), expected.quality) != 0) {
                    throw new IllegalStateException(id + " weights mismatch for " + tier + ": " + weight);
                }
            }
        }
        verifyAffixesAndGeneratedItems();
        LOGGER.info("Verified Ancient, Artifact, Heirloom, and Esoteric rarity codecs, ordering, weights, and original rule sequences.");
        if ("true".equalsIgnoreCase(System.getenv("JEICY_VERIFY_AND_STOP"))) {
            event.getServer().execute(() -> event.getServer().halt(false));
        }
    }

    private static void verifyAffixesAndGeneratedItems() {
        String[] affixPaths = {
            "armor/attribute/artifact_armor", "armor/attribute/artifact_hp",
            "armor/attribute/artifact_luck", "armor/attribute/artifact_toughness",
            "armor/attribute/esoteric_armor", "armor/attribute/esoteric_hp",
            "armor/attribute/esoteric_luck", "armor/attribute/esoteric_toughness"
        };
        LootRarity artifact = rarity("artifact");
        LootRarity esoteric = rarity("esoteric");
        for (String path : affixPaths) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
            Affix affix = AffixRegistry.INSTANCE.getValue(id);
            if (affix == null || affix.definition().type() != AffixType.STAT) {
                throw new IllegalStateException("Missing or invalid runtime affix " + id);
            }
            LootRarity targetRarity = path.contains("esoteric_") ? esoteric : artifact;
            if (!affix.canApplyTo(new ItemStack(Items.DIAMOND_CHESTPLATE),
                dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(new ItemStack(Items.DIAMOND_CHESTPLATE)),
                targetRarity)) {
                throw new IllegalStateException(id + " rejected its original chestplate category");
            }
            if (affix.canApplyTo(new ItemStack(Items.DIAMOND_SWORD),
                dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(new ItemStack(Items.DIAMOND_SWORD)),
                targetRarity)) {
                throw new IllegalStateException(id + " widened into the sword category");
            }
        }

        verifyGenerated("artifact", 4, 0.5F, 2);
        verifyGenerated("heirloom", 1, 0.7F, 1);
        verifyGenerated("esoteric", 5, 0.7F, 4);
        LOGGER.info("Verified 8 high-tier armor affixes and generated Artifact/Heirloom/Esoteric chestplates with exact sockets and stepped durability.");
    }

    private static void verifyGenerated(String rarityName, int expectedSockets, float durabilityBase,
        int minimumAffixes) {
        ItemStack stack = new ItemStack(Items.DIAMOND_CHESTPLATE);
        LootController.createLootItem(stack, rarity(rarityName),
            GenContext.dummy(RandomSource.create(0x4A45494359L + expectedSockets)));
        int sockets = SocketHelper.getSockets(stack);
        if (sockets != expectedSockets) {
            throw new IllegalStateException(rarityName + " generated " + sockets
                + " sockets instead of " + expectedSockets);
        }
        ItemAffixes affixes = stack.getOrDefault(Apoth.Components.AFFIXES, ItemAffixes.EMPTY);
        if (affixes.size() < minimumAffixes) {
            throw new IllegalStateException(rarityName + " generated only " + affixes.size()
                + " affixes, expected at least " + minimumAffixes);
        }
        float durability = stack.getOrDefault(Apoth.Components.DURABILITY_BONUS, -1.0F);
        float step = (durability - (durabilityBase - 0.07F)) / 0.01F;
        if (durability < durabilityBase - 0.070001F || durability > durabilityBase + 0.070001F
            || Math.abs(step - Math.round(step)) > 0.0001F) {
            throw new IllegalStateException(rarityName + " generated invalid durability " + durability);
        }
    }

    private static LootRarity rarity(String name) {
        return RarityRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(MOD_ID, name));
    }

    private static net.minecraft.world.item.Item material(String rarity) {
        return switch (rarity) {
            case "ancient" -> JeicyApotheoticAddition.ANCIENT_MATERIAL.get();
            case "artifact" -> JeicyApotheoticAddition.ARTIFACT_MATERIAL.get();
            case "heirloom" -> JeicyApotheoticAddition.HEIRLOOM_MATERIAL.get();
            case "esoteric" -> JeicyApotheoticAddition.ESOTERIC_MATERIAL.get();
            default -> throw new IllegalArgumentException(rarity);
        };
    }

    private record Expected(int sortIndex, int weight, float quality, int removedRules,
        int affixRules, int socketRules, float durability) {}
}
