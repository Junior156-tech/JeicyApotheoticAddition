package org.jeicy.apotheoticadditions;

/** Pure helpers shared by the runtime rarity codec and isolated unit tests. */
public final class OriginalRarityMath {
    private OriginalRarityMath() {}

    /** The old AffixHelper.step(-0.07, 14, 0.01) distribution. */
    public static float steppedDurability(float base, float randomValue) {
        int step = Math.min(14, (int) (randomValue * 15.0F));
        return base - 0.07F + step * 0.01F;
    }
}
