package org.jeicy.apotheoticadditions;

import java.util.Set;

/** Original 1.20.1 gem_class membership. Pure Java reference. NOT an Apotheosis registry hook. */
public final class OriginalSocketRules {
    private OriginalSocketRules() {}
    public static boolean appliesTo(Set<String> originalCategories, String requestedCategory) {
        return originalCategories.contains(requestedCategory);
    }
    // Never replace sword with the broader apotheosis:melee_weapon, which also allows axes.
    public static boolean ignisApplies(String itemClass) {
        return Set.of("sword", "trident").contains(itemClass);
    }
    public static boolean nagaApplies(String itemClass) {
        return "heavy_weapon".equals(itemClass);
    }
    public static boolean maledictusApplies(String itemClass) {
        return "pickaxe".equals(itemClass);
    }
}
