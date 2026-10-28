package org.jeicy.apotheoticadditions;

import dev.shadowsoffire.apotheosis.socket.gem.GemItem;
import net.minecraft.world.item.ItemStack;

/**
 * Read/write the nine-level original rarity axis on a 1.21.1 Apotheosis GemItem.
 * Does not modify GemItem's native purity component, so the values cannot
 * masquerade as PERFECT or silently override vanilla gem cutting rules.
 */
public final class JeicyGemRankAccess {
    private JeicyGemRankAccess() {}

    public static boolean isGem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof GemItem;
    }

    /** An unassigned vanilla GemItem has no original Cisco-tier designation. */
    public static OriginalGemRank get(ItemStack stack) {
        if (!isGem(stack)) return null;
        Integer stored = stack.get(JeicyGemComponents.ORIGINAL_GEM_RANK.get());
        return stored == null ? null : OriginalGemRank.fromIndex(stored);
    }

    public static void assign(ItemStack stack, OriginalGemRank rank) {
        if (!isGem(stack)) throw new IllegalArgumentException("Original rank requires Apotheosis GemItem");
        if (rank == null) throw new IllegalArgumentException("Rank cannot be null");
        stack.set(JeicyGemComponents.ORIGINAL_GEM_RANK.get(), rank.stableId());
    }

    /**
     * No direct arbitrary jump to Heirloom/Esoteric. Upgrade crafting must
     * separately validate actual original recipe ingredients and gem identity.
     */
    public static void advanceOne(ItemStack stack) {
        OriginalGemRank rank = get(stack);
        if (rank == null) throw new IllegalStateException("Gem has no original-tier designation");
        assign(stack, rank.next());
    }
}
