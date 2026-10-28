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
        assertSame(OriginalGemRank.ARTIFACT,
            OriginalGemRank.max(OriginalGemRank.ANCIENT, OriginalGemRank.ARTIFACT));
        assertSame(OriginalGemRank.MYTHIC, OriginalGemCatalog.minimumRank("aether/aether"));
        assertSame(OriginalGemRank.ARTIFACT, OriginalGemCatalog.minimumRank("crafted/cornelia"));
        assertSame(OriginalGemRank.COMMON, OriginalGemCatalog.minimumRank("modded/armor"));
        assertSame(OriginalGemRank.ARTIFACT, OriginalGemCatalog.initialRank("crafted/cornelia", 0));
        assertSame(OriginalGemRank.ANCIENT, OriginalGemCatalog.initialRank("modded/armor", 5));
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

        OriginalIntegerRankValues enchantments = new OriginalIntegerRankValues(Map.of(
            "apotheotic_additions:artifact", 2,
            "apotheotic_additions:heirloom", 3,
            "apotheotic_additions:esoteric", 4));
        assertEquals(2, enchantments.get(OriginalGemRank.ARTIFACT));
        assertEquals(4, enchantments.get(OriginalGemRank.ESOTERIC));
        assertNull(enchantments.get(OriginalGemRank.ANCIENT));
        assertThrows(IllegalArgumentException.class,
            () -> new OriginalIntegerRankValues(Map.of("common", -1)));
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

    @Test
    void preservesOriginalGemCuttingCosts() {
        for (int index = 0; index < OriginalGemRank.ESOTERIC.stableId(); index++) {
            OriginalGemRank rank = OriginalGemRank.fromIndex(index);
            assertEquals(1 + index * 2, OriginalGemUpgradeCosts.dustCost(rank));
            assertEquals(3, OriginalGemUpgradeCosts.materialCost(index, index));
            assertEquals(1, OriginalGemUpgradeCosts.materialCost(index, index + 1));
            if (index > 0) assertEquals(9,
                OriginalGemUpgradeCosts.materialCost(index, index - 1));
            assertEquals(-1, OriginalGemUpgradeCosts.materialCost(index, index + 2));
        }
        assertThrows(IllegalArgumentException.class,
            () -> OriginalGemUpgradeCosts.dustCost(OriginalGemRank.ESOTERIC));
    }
}
