package org.jeicy.apotheoticadditions;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Pure Java reference tier logic extracted from original JSON.
 * NOT YET A MINECRAFT RECIPE OR GEM BONUS HOOK.  The Minecraft integration
 * must register components and hook the Apotheosis GemCuttingRecipe and GemBonus implementations.
 */
public final class OriginalGemTierLogic {
    public static final List<String> TIERS = List.of(
        "common", "uncommon", "rare", "epic", "mythic", "ancient",
        "apotheotic_additions:artifact", "apotheotic_additions:heirloom",
        "apotheotic_additions:esoteric");
    private OriginalGemTierLogic() {}
    public static int index(String tier) {
        int index = TIERS.indexOf(Objects.requireNonNull(tier));
        if (index < 0) throw new IllegalArgumentException("Unknown original rarity: " + tier);
        return index;
    }
    public static String next(String tier) {
        int i = index(tier);
        if (i == TIERS.size() - 1) throw new IllegalStateException("Already Esoteric");
        return TIERS.get(i + 1);
    }
    /** Exact look-up; missing tier values are NOT extrapolated or renamed to Perfect. */
    public static <T> T originalValue(Map<String,T> bonusValues, String rarity) {
        index(rarity);
        return bonusValues.get(rarity);
    }
    public static boolean sameGem(String leftId, String rightId) {
        return Objects.equals(leftId, rightId) && leftId != null && leftId.contains(":");
    }
}
