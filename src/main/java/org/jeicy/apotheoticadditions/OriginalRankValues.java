package org.jeicy.apotheoticadditions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Exact numerical values for the nine *original* Cisco gem rarity levels.
 * Never interpolate missing levels and never substitute Purity.PERFECT.
 * No Minecraft dependency: regression-tested directly with javac.
 */
public final class OriginalRankValues {
    private final Map<OriginalGemRank, Double> byRank;
    private final Map<String, Double> serialized;

    public OriginalRankValues(Map<String, Double> source) {
        Objects.requireNonNull(source, "source");
        Map<OriginalGemRank, Double> ranks = new LinkedHashMap<>();
        Map<String, Double> keys = new LinkedHashMap<>();
        for (var entry : source.entrySet()) {
            OriginalGemRank rank = OriginalGemRank.fromOriginalId(entry.getKey());
            Double value = Objects.requireNonNull(entry.getValue(), "No value for " + entry.getKey());
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid non-finite gem value for " + entry.getKey());
            }
            ranks.put(rank, value);
            keys.put(rank.originalId(), value);
        }
        if (ranks.isEmpty()) throw new IllegalArgumentException("Gem rank values cannot be empty");
        this.byRank = Collections.unmodifiableMap(ranks);
        this.serialized = Collections.unmodifiableMap(keys);
    }

    /** Null means that this rank was not defined by the original gem. */
    public Double get(OriginalGemRank rank) {
        return byRank.get(rank);
    }

    public boolean contains(OriginalGemRank rank) {
        return byRank.containsKey(rank);
    }

    public Map<String, Double> serialized() {
        return serialized;
    }
}
