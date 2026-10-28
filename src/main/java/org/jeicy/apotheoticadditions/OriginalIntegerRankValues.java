package org.jeicy.apotheoticadditions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Exact integer values used by original rank-aware enchantment bonuses. */
public final class OriginalIntegerRankValues {
    private final Map<OriginalGemRank, Integer> byRank;
    private final Map<String, Integer> serialized;

    public OriginalIntegerRankValues(Map<String, Integer> source) {
        Objects.requireNonNull(source, "source");
        Map<OriginalGemRank, Integer> ranks = new LinkedHashMap<>();
        Map<String, Integer> keys = new LinkedHashMap<>();
        for (var entry : source.entrySet()) {
            OriginalGemRank rank = OriginalGemRank.fromOriginalId(entry.getKey());
            Integer value = Objects.requireNonNull(entry.getValue(), "No value for " + entry.getKey());
            if (value < 0) {
                throw new IllegalArgumentException("Negative enchantment value for " + entry.getKey());
            }
            ranks.put(rank, value);
            keys.put(rank.originalId(), value);
        }
        if (ranks.isEmpty()) throw new IllegalArgumentException("Gem rank values cannot be empty");
        this.byRank = Collections.unmodifiableMap(ranks);
        this.serialized = Collections.unmodifiableMap(keys);
    }

    public Integer get(OriginalGemRank rank) {
        return byRank.get(rank);
    }

    public Map<String, Integer> serialized() {
        return serialized;
    }
}
