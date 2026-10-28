package org.jeicy.apotheoticadditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

final class OriginalGemLogicTest {

    @Test
    void preservesAllNineStableRanksAndSequentialTransitions() {
        OriginalGemRank[] ranks = OriginalGemRank.values();
        assertEquals(9, ranks.length);

        for (int index = 0; index < ranks.length; index++) {
            OriginalGemRank rank = ranks[index];
            assertEquals(index, rank.stableId());
            assertSame(rank, OriginalGemRank.fromIndex(index));
            assertSame(rank, OriginalGemRank.fromOriginalId(rank.originalId()));
            if (index < ranks.length - 1) assertSame(ranks[index + 1], rank.next());
        }

        assertThrows(IllegalStateException.class, OriginalGemRank.ESOTERIC::next);
        assertThrows(IllegalArgumentException.class, () -> OriginalGemRank.fromIndex(-1));
        assertThrows(IllegalArgumentException.class, () -> OriginalGemRank.fromIndex(9));
        assertThrows(IllegalArgumentException.class, () -> OriginalGemRank.fromOriginalId("perfect"));
    }

    @Test
    void neverInterpolatesMissingOriginalValues() {
        OriginalRankValues values = new OriginalRankValues(Map.of(
            "apotheotic_additions:artifact", 4.5,
            "apotheotic_additions:heirloom", 5.0,
            "apotheotic_additions:esoteric", 6.0));

        assertEquals(4.5, values.get(OriginalGemRank.ARTIFACT));
        assertEquals(5.0, values.get(OriginalGemRank.HEIRLOOM));
        assertEquals(6.0, values.get(OriginalGemRank.ESOTERIC));
        assertNull(values.get(OriginalGemRank.ANCIENT));
        assertEquals(3, values.serialized().size());
        assertThrows(IllegalArgumentException.class,
            () -> new OriginalRankValues(Map.of("perfect", 9.0)));
        assertThrows(IllegalArgumentException.class,
            () -> new OriginalRankValues(Map.of("common", Double.NaN)));
        assertThrows(IllegalArgumentException.class,
            () -> new OriginalRankValues(Map.of()));
    }

    @Test
    void preservesTierLookupAndGemIdentity() {
        assertEquals(9, OriginalGemTierLogic.TIERS.size());
        for (int index = 0; index < OriginalGemTierLogic.TIERS.size() - 1; index++) {
            assertEquals(OriginalGemTierLogic.TIERS.get(index + 1),
                OriginalGemTierLogic.next(OriginalGemTierLogic.TIERS.get(index)));
        }

        assertEquals(0.95, OriginalGemTierLogic.originalValue(
            Map.of("apotheotic_additions:heirloom", 0.95),
            "apotheotic_additions:heirloom"));
        assertThrows(IllegalStateException.class,
            () -> OriginalGemTierLogic.next("apotheotic_additions:esoteric"));
        assertFalse(OriginalGemTierLogic.sameGem(
            "apotheotic_additions:crafted/ignis", "apotheotic_additions:crafted/naga"));
        assertTrue(OriginalGemTierLogic.sameGem(
            "apotheotic_additions:crafted/ignis", "apotheotic_additions:crafted/ignis"));
    }

    @Test
    void preservesExactOriginalEquipmentRestrictions() {
        assertTrue(OriginalSocketRules.ignisApplies("sword"));
        assertTrue(OriginalSocketRules.ignisApplies("trident"));
        assertFalse(OriginalSocketRules.ignisApplies("heavy_weapon"));
        assertTrue(OriginalSocketRules.nagaApplies("heavy_weapon"));
        assertFalse(OriginalSocketRules.nagaApplies("sword"));
        assertTrue(OriginalSocketRules.maledictusApplies("pickaxe"));
        assertFalse(OriginalSocketRules.maledictusApplies("shovel"));
        assertTrue(OriginalSocketRules.appliesTo(Set.of("sword", "trident"), "sword"));
        assertFalse(OriginalSocketRules.appliesTo(Set.of("sword", "trident"), "axe"));
    }
}
