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
        Map.entry("crafted/frostmaw", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/hydra", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ignis", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/maledictus", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/monstrosity", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/slider", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/snow_queen", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/warden", OriginalGemRank.ARTIFACT),
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
        LOGGER.info("Verified 4 exact original socket-category decisions.");
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
}
