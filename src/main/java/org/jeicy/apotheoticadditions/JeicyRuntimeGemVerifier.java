package org.jeicy.apotheoticadditions;

import java.util.Map;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.shadowsoffire.apotheosis.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/** Runtime assertion that proves the original-rank component survives Gem creation. */
public final class JeicyRuntimeGemVerifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, OriginalGemRank> ACTIVE_GEMS = Map.ofEntries(
        Map.entry("crafted/cornelia", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ender_dragon", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/frostmaw", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/hydra", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ignis", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/maledictus", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/monstrosity", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/slider", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/snow_queen", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/warden", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/wither", OriginalGemRank.ARTIFACT),
        Map.entry("modded/armor", OriginalGemRank.COMMON),
        Map.entry("modded/hearty", OriginalGemRank.COMMON)
    );

    private JeicyRuntimeGemVerifier() {}

    public static void onServerStarted(ServerStartedEvent event) {
        int verified = 0;
        for (var entry : ACTIVE_GEMS.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                "apotheotic_additions", entry.getKey());
            Gem gem = GemRegistry.INSTANCE.getValue(id);
            if (gem == null) {
                throw new IllegalStateException("Active original gem was not registered: " + id);
            }

            for (Purity purity : Purity.values()) {
                ItemStack stack = gem.toStack(purity);
                OriginalGemRank actual = JeicyGemRankAccess.get(stack);
                OriginalGemRank expected = OriginalGemCatalog.initialRank(
                    entry.getKey(), purity.ordinal());
                if (actual != expected) {
                    throw new IllegalStateException("Original rank mismatch for " + id + " / "
                        + purity.getSerializedName() + ": expected " + expected + ", got " + actual);
                }
                verified++;
            }
        }
        LOGGER.info("Verified {} original gem rank assignments across {} active gems.",
            verified, ACTIVE_GEMS.size());

        verifySocketRule("crafted/hydra", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("crafted/hydra", net.minecraft.world.item.Items.DIAMOND_AXE, false);
        verifySocketRule("crafted/maledictus", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("crafted/maledictus", net.minecraft.world.item.Items.DIAMOND_SHOVEL, false);
        verifySocketRule("crafted/ender_dragon", net.minecraft.world.item.Items.BOW, true);
        verifySocketRule("crafted/ender_dragon", net.minecraft.world.item.Items.CROSSBOW, true);
        verifyWitherProfile(net.minecraft.world.item.Items.DIAMOND_SWORD, 0);
        verifyWitherProfile(net.minecraft.world.item.Items.TRIDENT, 0);
        verifyWitherProfile(net.minecraft.world.item.Items.DIAMOND_AXE, 1);
        LOGGER.info("Verified 6 exact socket decisions and 3 split weapon profiles.");
        verifyEnderDragonEnchantments(event);
        LOGGER.info("Verified Ender Dragon Artifact rank adds 2 levels to all existing enchantments.");
    }

    private static void verifySocketRule(String gemPath, net.minecraft.world.item.Item target,
                                         boolean expected) {
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", gemPath));
        ItemStack gemStack = gem.toStack(Purity.PERFECT);
        boolean actual = gem.canApplyTo(new ItemStack(target), gemStack, Purity.PERFECT);
        if (actual != expected) {
            throw new IllegalStateException("Socket rule mismatch for " + gemPath + " on "
                + target + ": expected " + expected + ", got " + actual);
        }
    }

    private static void verifyWitherProfile(net.minecraft.world.item.Item target, int expected) {
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "crafted/wither"));
        ItemStack targetStack = new ItemStack(target);
        var bonus = gem.getBonus(dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(targetStack),
            Purity.PERFECT).orElseThrow();
        if (!(bonus instanceof JeicyTieredMultiAttributeBonus multi)) {
            throw new IllegalStateException("Wither gem did not load its original multi-attribute bonus");
        }
        int actual = multi.profileIndexFor(targetStack);
        if (actual != expected) {
            throw new IllegalStateException("Wither profile mismatch for " + target
                + ": expected " + expected + ", got " + actual);
        }
    }

    private static void verifyEnderDragonEnchantments(ServerStartedEvent serverEvent) {
        var lookup = serverEvent.getServer().registryAccess()
            .lookup(net.minecraft.core.registries.Registries.ENCHANTMENT).orElseThrow();
        var sharpness = lookup.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SHARPNESS);
        var unbreaking = lookup.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING);
        var enchantments = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(
            net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        enchantments.set(sharpness, 3);
        enchantments.set(unbreaking, 2);

        ItemStack targetStack = new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "crafted/ender_dragon"));
        ItemStack gemStack = gem.toStack(Purity.PERFECT);
        var category = dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(targetStack);
        var instance = dev.shadowsoffire.apotheosis.socket.gem.GemInstance.socketed(
            category, gemStack, 0);
        var bonus = gem.getBonus(category, Purity.PERFECT).orElseThrow();
        var levelEvent = new net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent(
            targetStack, enchantments, sharpness, lookup);
        bonus.getEnchantmentLevels(instance, levelEvent);

        if (enchantments.getLevel(sharpness) != 5 || enchantments.getLevel(unbreaking) != 4) {
            throw new IllegalStateException("Ender Dragon global enchantment bonus did not preserve +2");
        }
    }
}
