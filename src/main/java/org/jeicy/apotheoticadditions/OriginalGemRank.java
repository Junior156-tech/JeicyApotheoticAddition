package org.jeicy.apotheoticadditions;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Faithful rarity axis of Apotheotic Additions 2.2.2.  This is NOT a renamed
 * Apotheosis 1.21 Gem Purity: the new mod represents purity separately.
 * Stable integer IDs are stored on gem ItemStacks and must never be reordered.
 */
public enum OriginalGemRank {
    COMMON(0, "common"),
    UNCOMMON(1, "uncommon"),
    RARE(2, "rare"),
    EPIC(3, "epic"),
    MYTHIC(4, "mythic"),
    ANCIENT(5, "ancient"),
    ARTIFACT(6, "apotheotic_additions:artifact"),
    HEIRLOOM(7, "apotheotic_additions:heirloom"),
    ESOTERIC(8, "apotheotic_additions:esoteric");

    private static final OriginalGemRank[] BY_INDEX = values();
    private static final Map<String, OriginalGemRank> BY_ID = Arrays.stream(values())
        .collect(Collectors.toUnmodifiableMap(OriginalGemRank::originalId, Function.identity()));

    private final int stableId;
    private final String originalId;

    OriginalGemRank(int stableId, String originalId) {
        this.stableId = stableId;
        this.originalId = originalId;
    }

    public int stableId() { return stableId; }
    public String originalId() { return originalId; }

    public static OriginalGemRank fromIndex(int index) {
        if (index < 0 || index >= BY_INDEX.length)
            throw new IllegalArgumentException("Invalid original gem rarity: " + index);
        return BY_INDEX[index];
    }

    public static OriginalGemRank fromOriginalId(String id) {
        OriginalGemRank rank = BY_ID.get(id);
        if (rank == null) throw new IllegalArgumentException("Unknown original rarity: " + id);
        return rank;
    }

    public boolean canAdvance() { return stableId < ESOTERIC.stableId; }

    /** Only a sequential upgrade is permitted. Material consumption is checked elsewhere. */
    public OriginalGemRank next() {
        if (!canAdvance()) throw new IllegalStateException("Esoteric is already the maximum rank");
        return fromIndex(stableId + 1);
    }
}
