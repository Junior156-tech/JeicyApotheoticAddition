package org.jeicy.apotheoticadditions;

/** Pure representation of Apotheosis 1.20's original gem-cutting cost formula. */
public final class OriginalGemUpgradeCosts {
    private OriginalGemUpgradeCosts() {}

    public static int dustCost(OriginalGemRank rank) {
        if (!rank.canAdvance()) throw new IllegalArgumentException("Esoteric cannot be upgraded");
        return 1 + rank.stableId() * 2;
    }

    public static int materialCost(int gemRank, int materialRank) {
        if (materialRank == gemRank) return 3;
        if (materialRank == gemRank + 1) return 1;
        if (materialRank == gemRank - 1) return 9;
        return -1;
    }
}
