package org.jeicy.apotheoticadditions;

import java.util.Map;

/** Exact minimum original rarity declared by all 67 version 2.2.2 gems. */
public final class OriginalGemCatalog {
    private static final Map<String, OriginalGemRank> MINIMUM_RANKS = Map.ofEntries(
        Map.entry("aether/aether", OriginalGemRank.MYTHIC),
        Map.entry("aether/candied", OriginalGemRank.RARE),
        Map.entry("aether/purity", OriginalGemRank.RARE),
        Map.entry("aether/zanite_alchemist", OriginalGemRank.RARE),
        Map.entry("blue_skies/arachnarch", OriginalGemRank.RARE),
        Map.entry("blue_skies/blue_skies", OriginalGemRank.RARE),
        Map.entry("blue_skies/horizonite_alchemist", OriginalGemRank.RARE),
        Map.entry("blue_skies/twin_magi", OriginalGemRank.RARE),
        Map.entry("blue_skies/ventium_alchemist", OriginalGemRank.RARE),
        Map.entry("bumblezone/bee_queen", OriginalGemRank.UNCOMMON),
        Map.entry("bumblezone/hive", OriginalGemRank.RARE),
        Map.entry("crafted/cornelia", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ender_dragon", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/frostmaw", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/harbringer", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/hydra", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ignis", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/leviathan", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/lich", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/maledictus", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/monstrosity", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/naga", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/remnant", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/slider", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/snow_queen", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/sunbird", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ur-ghast", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/warden", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/wither", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/wroughtnaut", OriginalGemRank.ARTIFACT),
        Map.entry("curios/reinforced", OriginalGemRank.UNCOMMON),
        Map.entry("curios/twisted", OriginalGemRank.UNCOMMON),
        Map.entry("curios/vitalic", OriginalGemRank.UNCOMMON),
        Map.entry("midnight/darkheart", OriginalGemRank.RARE),
        Map.entry("midnight/midnight", OriginalGemRank.RARE),
        Map.entry("modded/amber", OriginalGemRank.RARE),
        Map.entry("modded/archmage", OriginalGemRank.RARE),
        Map.entry("modded/bloodcaster", OriginalGemRank.RARE),
        Map.entry("modded/cataclysm", OriginalGemRank.RARE),
        Map.entry("modded/depths", OriginalGemRank.EPIC),
        Map.entry("modded/draconic_legacy", OriginalGemRank.UNCOMMON),
        Map.entry("modded/dracula", OriginalGemRank.RARE),
        Map.entry("modded/engineer", OriginalGemRank.RARE),
        Map.entry("modded/felfire", OriginalGemRank.UNCOMMON),
        Map.entry("modded/flesh", OriginalGemRank.UNCOMMON),
        Map.entry("modded/flux", OriginalGemRank.RARE),
        Map.entry("modded/heartland", OriginalGemRank.EPIC),
        Map.entry("modded/marrow", OriginalGemRank.UNCOMMON),
        Map.entry("modded/midas", OriginalGemRank.EPIC),
        Map.entry("modded/mystic", OriginalGemRank.RARE),
        Map.entry("modded/rat_chef", OriginalGemRank.UNCOMMON),
        Map.entry("modded/safari", OriginalGemRank.RARE),
        Map.entry("modded/soultwisted", OriginalGemRank.RARE),
        Map.entry("modded/thermal", OriginalGemRank.UNCOMMON),
        Map.entry("modded/uranium", OriginalGemRank.RARE),
        Map.entry("modded/weeping_well", OriginalGemRank.EPIC),
        Map.entry("modded/witch_queen", OriginalGemRank.UNCOMMON),
        Map.entry("modded/witherstorm", OriginalGemRank.RARE),
        Map.entry("otherside/sculk", OriginalGemRank.RARE),
        Map.entry("twilight/umbral_matrimony", OriginalGemRank.RARE),
        Map.entry("twilight/wildheart", OriginalGemRank.RARE),
        Map.entry("undergarden/decay", OriginalGemRank.RARE),
        Map.entry("undergarden/forlorn", OriginalGemRank.RARE),
        Map.entry("undergarden/undergarden", OriginalGemRank.RARE)
    );

    private OriginalGemCatalog() {}

    /** Gems without min_rarity in 2.2.2 begin at COMMON. */
    public static OriginalGemRank minimumRank(String gemPath) {
        return MINIMUM_RANKS.getOrDefault(gemPath, OriginalGemRank.COMMON);
    }

    public static OriginalGemRank initialRank(String gemPath, int purityOrdinal) {
        OriginalGemRank purityRank = OriginalGemRank.fromIndex(purityOrdinal);
        return OriginalGemRank.max(purityRank, minimumRank(gemPath));
    }
}
