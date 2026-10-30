package org.jeicy.apotheoticadditions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.shadowsoffire.apotheosis.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/** Runtime assertion that proves the original-rank component survives Gem creation. */
public final class JeicyRuntimeGemVerifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, OriginalGemRank> ACTIVE_GEMS = Map.ofEntries(
        Map.entry("crafted/cornelia", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ender_dragon", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/frostmaw", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/hydra", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ignis", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/maledictus", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/monstrosity", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/slider", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/snow_queen", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/ur-ghast", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/warden", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/wither", OriginalGemRank.ARTIFACT),
        Map.entry("crafted/wroughtnaut", OriginalGemRank.ARTIFACT),
        Map.entry("modded/armor", OriginalGemRank.COMMON),
        Map.entry("modded/hearty", OriginalGemRank.COMMON),
        Map.entry("modded/midas", OriginalGemRank.EPIC)
    );

    private JeicyRuntimeGemVerifier() {}

    public static void onServerStarted(ServerStartedEvent event) {
        Map<String, OriginalGemRank> activeGems = new LinkedHashMap<>(ACTIVE_GEMS);
        if (net.neoforged.fml.ModList.get().isLoaded("aether")) {
            activeGems.put("aether/aether", OriginalGemRank.MYTHIC);
            activeGems.put("aether/purity", OriginalGemRank.RARE);
            activeGems.put("aether/zanite_alchemist", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("twilightforest")) {
            activeGems.put("crafted/naga", OriginalGemRank.ARTIFACT);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("undergarden")) {
            activeGems.put("undergarden/decay", OriginalGemRank.RARE);
            activeGems.put("undergarden/undergarden", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("farmersdelight")) {
            activeGems.put("modded/heartland", OriginalGemRank.EPIC);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("cataclysm")) {
            activeGems.put("crafted/harbringer", OriginalGemRank.ARTIFACT);
            activeGems.put("crafted/leviathan", OriginalGemRank.ARTIFACT);
            activeGems.put("crafted/remnant", OriginalGemRank.ARTIFACT);
            activeGems.put("modded/cataclysm", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("ars_nouveau")) {
            activeGems.put("modded/archmage", OriginalGemRank.RARE);
            activeGems.put("modded/ars_mana", OriginalGemRank.COMMON);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("the_bumblezone")) {
            activeGems.put("bumblezone/bee_queen", OriginalGemRank.UNCOMMON);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("born_in_chaos_v1")) {
            activeGems.put("modded/felfire", OriginalGemRank.UNCOMMON);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("enderio")) {
            activeGems.put("modded/engineer", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("vampirism")) {
            activeGems.put("modded/dracula", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("malum")) {
            activeGems.put("modded/weeping_well", OriginalGemRank.EPIC);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")) {
            activeGems.put("modded/soultwisted", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")
            && net.neoforged.fml.ModList.get().isLoaded("twilightforest")) {
            activeGems.put("crafted/lich", OriginalGemRank.ARTIFACT);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("curios")) {
            activeGems.put("curios/reinforced", OriginalGemRank.UNCOMMON);
            activeGems.put("curios/twisted", OriginalGemRank.UNCOMMON);
            activeGems.put("curios/vitalic", OriginalGemRank.UNCOMMON);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("aether")
            && net.neoforged.fml.ModList.get().isLoaded("curios")
            && net.neoforged.fml.ModList.get().isLoaded("twilightforest")) {
            activeGems.put("twilight/umbral_matrimony", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("irons_spellbooks")
            && net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")
            && net.neoforged.fml.ModList.get().isLoaded("curios")) {
            activeGems.put("modded/bloodcaster", OriginalGemRank.RARE);
            activeGems.put("modded/mystic", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("irons_spellbooks")
            && net.neoforged.fml.ModList.get().isLoaded("aether")
            && net.neoforged.fml.ModList.get().isLoaded("curios")
            && net.neoforged.fml.ModList.get().isLoaded("the_bumblezone")) {
            activeGems.put("bumblezone/hive", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("aether")
            && net.neoforged.fml.ModList.get().isLoaded("curios")
            && net.neoforged.fml.ModList.get().isLoaded("deeperdarker")) {
            activeGems.put("otherside/sculk", OriginalGemRank.RARE);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")
            && net.neoforged.fml.ModList.get().isLoaded("ad_astra")) {
            activeGems.put("modded/witch_queen", OriginalGemRank.UNCOMMON);
        }
        int verified = 0;
        for (var entry : activeGems.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                "apotheotic_additions", entry.getKey());
            Gem gem = GemRegistry.INSTANCE.getValue(id);
            if (gem == null) {
                throw new IllegalStateException("Active original gem was not registered: " + id);
            }

            for (Purity purity : Purity.values()) {
                ItemStack stack = gem.toStack(purity);
                OriginalGemRank actual = JeicyGemRankAccess.get(stack);
                OriginalGemRank expected = OriginalGemCatalog.initialRank(
                    entry.getKey(), purity.ordinal());
                if (actual != expected) {
                    throw new IllegalStateException("Original rank mismatch for " + id + " / "
                        + purity.getSerializedName() + ": expected " + expected + ", got " + actual);
                }
                verified++;
            }
        }
        LOGGER.info("Verified {} original gem rank assignments across {} active gems.",
            verified, activeGems.size());

        verifySocketRule("crafted/hydra", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("crafted/hydra", net.minecraft.world.item.Items.DIAMOND_AXE, false);
        verifySocketRule("crafted/maledictus", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("crafted/maledictus", net.minecraft.world.item.Items.DIAMOND_SHOVEL, false);
        verifySocketRule("crafted/ender_dragon", net.minecraft.world.item.Items.BOW, true);
        verifySocketRule("crafted/ender_dragon", net.minecraft.world.item.Items.CROSSBOW, true);
        verifySocketRule("crafted/ur-ghast", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("crafted/ur-ghast", net.minecraft.world.item.Items.DIAMOND_HELMET, false);
        verifySocketRule("crafted/wroughtnaut", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("crafted/wroughtnaut", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        verifySocketRule("modded/midas", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("modded/midas", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, false);
        verifySocketRule("modded/midas", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/midas", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("modded/midas", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("modded/midas", net.minecraft.world.item.Items.DIAMOND_PICKAXE, false);
        verifyMultiAttributeProfile("crafted/wither", net.minecraft.world.item.Items.DIAMOND_SWORD, 0);
        verifyMultiAttributeProfile("crafted/wither", net.minecraft.world.item.Items.TRIDENT, 0);
        verifyMultiAttributeProfile("crafted/wither", net.minecraft.world.item.Items.DIAMOND_AXE, 1);
        verifyMultiAttributeProfile("crafted/wroughtnaut", net.minecraft.world.item.Items.DIAMOND_AXE, 0);
        LOGGER.info("Verified 16 exact socket decisions and 4 multi-attribute profiles.");
        verifyUrGhastAllStats();
        LOGGER.info("Verified Ur-Ghast Artifact/Heirloom/Esoteric values across 28 attributes.");
        verifyMidas(event);
        LOGGER.info("Verified Midas helmet defense, weapon profiles, enchantment, and drop values.");
        if (net.neoforged.fml.ModList.get().isLoaded("aether")) {
            verifyAether();
            LOGGER.info("Verified Aether flight, alchemy, effects, values, ingredients, and exact sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("twilightforest")) {
            verifyNaga();
            LOGGER.info("Verified Naga log transformation, values, ingredient, output, and heavy-weapon socket.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("undergarden")) {
            verifyUndergarden();
            LOGGER.info("Verified Undergarden alchemy, attributes, Virulence values, and exact sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("farmersdelight")) {
            verifyHeartland(event);
            LOGGER.info("Verified Heartland alchemy, Backstabbing, Comfort values, and exact sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("cataclysm")) {
            verifyCataclysmBossGems();
            LOGGER.info("Verified Harbringer, Leviathan, and Remnant effects, values, and exact sockets.");
            verifyCataclysmGeneralGem();
            LOGGER.info("Verified Cataclysm split weapon effects, stacking, values, and exact sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("ars_nouveau")) {
            verifyArsNouveauGems();
            LOGGER.info("Verified Archmage and Ars Mana attributes, enchantment, effect, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("the_bumblezone")) {
            verifyBeeQueen();
            LOGGER.info("Verified Bee Queen stacking, attributes, values, and exact sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("born_in_chaos_v1")) {
            verifyFelfire();
            LOGGER.info("Verified Felfire alchemy, stacking, attributes, enchantment, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("enderio")) {
            verifyEngineer();
            LOGGER.info("Verified Engineer Withering, reach attributes, values, and exact sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("vampirism")) {
            verifyDracula();
            LOGGER.info("Verified Dracula enchantment, effects, attribute profiles, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("malum")) {
            verifyWeepingWell();
            LOGGER.info("Verified Weeping Well Malum attributes, enchantment, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")) {
            verifySoultwisted();
            LOGGER.info("Verified Soultwisted effects, restored wand category, attributes, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")
            && net.neoforged.fml.ModList.get().isLoaded("twilightforest")) {
            verifyLich();
            LOGGER.info("Verified Lich wand category, magic-power values, and exact socket.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("curios")) {
            verifyCuriosGems();
            LOGGER.info("Verified Reinforced, Twisted, and Vitalic Curios categories, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("aether")
            && net.neoforged.fml.ModList.get().isLoaded("curios")
            && net.neoforged.fml.ModList.get().isLoaded("twilightforest")) {
            verifyUmbralMatrimony();
            LOGGER.info("Verified Umbral Matrimony alchemy, Curios/Aether categories, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("irons_spellbooks")
            && net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")
            && net.neoforged.fml.ModList.get().isLoaded("curios")) {
            verifyIronSpellbookGems();
            LOGGER.info("Verified Bloodcaster and Mystic effects, spellbook attributes, alchemy, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("irons_spellbooks")
            && net.neoforged.fml.ModList.get().isLoaded("aether")
            && net.neoforged.fml.ModList.get().isLoaded("curios")
            && net.neoforged.fml.ModList.get().isLoaded("the_bumblezone")) {
            verifyHive();
            LOGGER.info("Verified Hive Curios/Aether/spellbook attributes, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("aether")
            && net.neoforged.fml.ModList.get().isLoaded("curios")
            && net.neoforged.fml.ModList.get().isLoaded("deeperdarker")) {
            verifySculk();
            LOGGER.info("Verified Sculk alchemy, Curios/Aether attributes, values, and sockets.");
        }
        if (net.neoforged.fml.ModList.get().isLoaded("eidolon_repraised")
            && net.neoforged.fml.ModList.get().isLoaded("ad_astra")) {
            verifyWitchQueen();
            LOGGER.info("Verified Witch Queen effects, attributes, lunar alchemy, values, and sockets.");
        }
        verifyConditionalGemsSkipped();
        LOGGER.info("Verified optional gem definitions are skipped when their source mods are absent.");
        verifyEnderDragonEnchantments(event);
        LOGGER.info("Verified Ender Dragon Artifact rank adds 2 levels to all existing enchantments.");
        verifyOriginalRankEvolution(event);
        LOGGER.info("Verified native and custom cutting paths from Common through Esoteric.");
    }

    private static void verifySocketRule(String gemPath, net.minecraft.world.item.Item target,
                                         boolean expected) {
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", gemPath));
        ItemStack gemStack = gem.toStack(Purity.PERFECT);
        boolean actual = gem.canApplyTo(new ItemStack(target), gemStack, Purity.PERFECT);
        if (actual != expected) {
            throw new IllegalStateException("Socket rule mismatch for " + gemPath + " on "
                + target + ": expected " + expected + ", got " + actual);
        }
    }

    private static void verifyMultiAttributeProfile(String gemPath,
                                                     net.minecraft.world.item.Item target,
                                                     int expected) {
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", gemPath));
        ItemStack targetStack = new ItemStack(target);
        var bonus = gem.getBonus(dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(targetStack),
            Purity.PERFECT).orElseThrow();
        if (!(bonus instanceof JeicyTieredMultiAttributeBonus multi)) {
            throw new IllegalStateException(gemPath + " did not load its original multi-attribute bonus");
        }
        int actual = multi.profileIndexFor(targetStack);
        if (actual != expected) {
            throw new IllegalStateException(gemPath + " profile mismatch for " + target
                + ": expected " + expected + ", got " + actual);
        }
    }

    private static void verifyUrGhastAllStats() {
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "crafted/ur-ghast"));
        ItemStack targetStack = new ItemStack(net.minecraft.world.item.Items.DIAMOND_BOOTS);
        var bonus = gem.getBonus(dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(targetStack),
            Purity.PERFECT).orElseThrow();
        if (!(bonus instanceof JeicyTieredAllStatsBonus allStats)
            || allStats.attributeCount() != 28
            || !Double.valueOf(0.05).equals(allStats.valueForRank(OriginalGemRank.ARTIFACT))
            || !Double.valueOf(0.075).equals(allStats.valueForRank(OriginalGemRank.HEIRLOOM))
            || !Double.valueOf(0.1).equals(allStats.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Ur-Ghast all-stats definition did not preserve original values");
        }
    }

    private static void verifyMidas(ServerStartedEvent serverEvent) {
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "modded/midas"));
        ItemStack gemStack = gem.toStack(Purity.PERFECT);
        JeicyGemRankAccess.assign(gemStack, OriginalGemRank.ARTIFACT);

        ItemStack helmet = new ItemStack(net.minecraft.world.item.Items.DIAMOND_HELMET);
        var helmetBonus = gem.getBonus(
            dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(helmet), Purity.PERFECT)
            .orElseThrow();
        if (!(helmetBonus instanceof JeicyTieredMageSlayerBonus mageSlayer)
            || !Double.valueOf(0.2).equals(mageSlayer.valueForRank(OriginalGemRank.ARTIFACT))
            || !Double.valueOf(0.3).equals(mageSlayer.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Midas Mageslayer values did not match the original");
        }

        ItemStack sword = new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
        var category = dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(sword);
        var weaponBonus = gem.getBonus(category, Purity.PERFECT).orElseThrow();
        if (!(weaponBonus instanceof JeicyMidasWeaponBonus midas)
            || midas.enchantmentValueForRank(OriginalGemRank.ARTIFACT) != 2
            || !Double.valueOf(0.1).equals(midas.transformChanceForRank(OriginalGemRank.ARTIFACT))
            || !midas.outputForVerification().is(net.minecraft.world.item.Items.GOLDEN_APPLE)) {
            throw new IllegalStateException("Midas weapon values did not match the original");
        }

        var lookup = serverEvent.getServer().registryAccess()
            .lookup(net.minecraft.core.registries.Registries.ENCHANTMENT).orElseThrow();
        var sweeping = lookup.getOrThrow(
            net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE);
        var enchantments = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(
            net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        var event = new net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent(
            sword, enchantments, sweeping, lookup);
        var instance = dev.shadowsoffire.apotheosis.socket.gem.GemInstance.socketed(
            category, gemStack, 0);
        weaponBonus.getEnchantmentLevels(instance, event);
        if (enchantments.getLevel(sweeping) != 2) {
            throw new IllegalStateException("Midas Artifact rank did not add Sweeping Edge II");
        }
    }

    private static void verifyConditionalGemsSkipped() {
        Map<String, String> conditionalGems = Map.ofEntries(
            Map.entry("aether/aether", "aether"),
            Map.entry("aether/purity", "aether"),
            Map.entry("aether/zanite_alchemist", "aether"),
            Map.entry("crafted/naga", "twilightforest"),
            Map.entry("undergarden/decay", "undergarden"),
            Map.entry("undergarden/undergarden", "undergarden"),
            Map.entry("modded/heartland", "farmersdelight"),
            Map.entry("crafted/harbringer", "cataclysm"),
            Map.entry("crafted/leviathan", "cataclysm"),
            Map.entry("crafted/remnant", "cataclysm"),
            Map.entry("modded/cataclysm", "cataclysm"),
            Map.entry("modded/archmage", "ars_nouveau"),
            Map.entry("modded/ars_mana", "ars_nouveau"),
            Map.entry("bumblezone/bee_queen", "the_bumblezone"),
            Map.entry("modded/felfire", "born_in_chaos_v1"),
            Map.entry("modded/engineer", "enderio"),
            Map.entry("modded/dracula", "vampirism"),
            Map.entry("modded/weeping_well", "malum"),
            Map.entry("modded/soultwisted", "eidolon_repraised"),
            Map.entry("crafted/lich", "eidolon_repraised"),
            Map.entry("curios/reinforced", "curios"),
            Map.entry("curios/twisted", "curios"),
            Map.entry("curios/vitalic", "curios"),
            Map.entry("twilight/umbral_matrimony", "curios"),
            Map.entry("modded/bloodcaster", "irons_spellbooks"),
            Map.entry("modded/mystic", "irons_spellbooks"),
            Map.entry("bumblezone/hive", "irons_spellbooks"),
            Map.entry("otherside/sculk", "deeperdarker"),
            Map.entry("modded/witch_queen", "ad_astra"),
            Map.entry("crafted/sunbird", "mowziesmobs"));
        conditionalGems.forEach((gemPath, modId) -> {
            if (!net.neoforged.fml.ModList.get().isLoaded(modId)) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    "apotheotic_additions", gemPath);
                if (GemRegistry.INSTANCE.getValue(id) != null) {
                    throw new IllegalStateException("Conditional gem loaded without " + modId + ": " + id);
                }
            }
        });
    }

    private static void verifyAether() {
        verifySocketRule("aether/aether", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("aether/aether", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("aether/aether", net.minecraft.world.item.Items.DIAMOND_BOOTS, false);

        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "aether/aether"));
        var helmetBonus = gem.getBonus(dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(
            new ItemStack(net.minecraft.world.item.Items.DIAMOND_HELMET)), Purity.PERFECT).orElseThrow();
        var chestBonus = gem.getBonus(dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(
            new ItemStack(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE)), Purity.PERFECT).orElseThrow();
        if (!(helmetBonus instanceof JeicyTieredAttributeBonus helmet)
            || !(chestBonus instanceof JeicyTieredAttributeBonus chest)
            || !Double.valueOf(1).equals(helmet.valueForRank(OriginalGemRank.MYTHIC))
            || !Double.valueOf(0).equals(chest.valueForRank(OriginalGemRank.MYTHIC))
            || !Double.valueOf(1).equals(chest.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Aether flight values did not match the original");
        }

        verifySocketRule("aether/purity", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("aether/purity", net.minecraft.world.item.Items.DIAMOND_SHOVEL, false);
        verifySocketRule("aether/purity", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("aether/purity", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("aether/purity", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("aether/purity", net.minecraft.world.item.Items.DIAMOND_HELMET, false);

        Gem purity = gem("aether/purity");
        JeicyTieredDropTransformBonus purityDrop = requireBonus(
            purity, net.minecraft.world.item.Items.DIAMOND_PICKAXE,
            JeicyTieredDropTransformBonus.class);
        JeicyTieredAttributeBonus purityWeapon = requireBonus(
            purity, net.minecraft.world.item.Items.DIAMOND_SWORD,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus purityArmor = requireBonus(
            purity, net.minecraft.world.item.Items.DIAMOND_BOOTS,
            JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(0.15).equals(purityDrop.chanceForRank(OriginalGemRank.RARE))
            || !Double.valueOf(1).equals(purityDrop.chanceForRank(OriginalGemRank.ESOTERIC))
            || !purityDrop.acceptsForVerification(new ItemStack(net.minecraft.world.item.Items.COBBLESTONE))
            || !purityDrop.outputForVerification().is(item("aether:holystone"))
            || !Double.valueOf(0.015).equals(purityWeapon.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.11).equals(purityWeapon.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.05).equals(purityArmor.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.35).equals(purityArmor.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Aether Purity values did not match the original");
        }

        verifySocketRule("aether/zanite_alchemist", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("aether/zanite_alchemist", net.minecraft.world.item.Items.DIAMOND_SHOVEL, false);
        verifySocketRule("aether/zanite_alchemist", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("aether/zanite_alchemist", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("aether/zanite_alchemist", net.minecraft.world.item.Items.DIAMOND_SWORD, false);

        Gem zanite = gem("aether/zanite_alchemist");
        JeicyTieredDropTransformBonus zaniteDrop = requireBonus(
            zanite, net.minecraft.world.item.Items.DIAMOND_PICKAXE,
            JeicyTieredDropTransformBonus.class);
        JeicyTieredMobEffectBonus zaniteChest = requireBonus(
            zanite, net.minecraft.world.item.Items.DIAMOND_CHESTPLATE,
            JeicyTieredMobEffectBonus.class);
        JeicyTieredMobEffectBonus zaniteHeavy = requireBonus(
            zanite, net.minecraft.world.item.Items.DIAMOND_AXE,
            JeicyTieredMobEffectBonus.class);
        var chestRare = zaniteChest.valueForRank(OriginalGemRank.RARE);
        var chestEsoteric = zaniteChest.valueForRank(OriginalGemRank.ESOTERIC);
        var heavyRare = zaniteHeavy.valueForRank(OriginalGemRank.RARE);
        var heavyEsoteric = zaniteHeavy.valueForRank(OriginalGemRank.ESOTERIC);
        if (!Double.valueOf(0.15).equals(zaniteDrop.chanceForRank(OriginalGemRank.RARE))
            || !Double.valueOf(1).equals(zaniteDrop.chanceForRank(OriginalGemRank.ESOTERIC))
            || !zaniteDrop.acceptsForVerification(new ItemStack(net.minecraft.world.item.Items.RAW_IRON))
            || !zaniteDrop.outputForVerification().is(item("aether:zanite_gemstone"))
            || chestRare == null || chestRare.cooldown() != 1500 || chestRare.amplifier() != 0
            || chestEsoteric == null || chestEsoteric.cooldown() != 1000 || chestEsoteric.amplifier() != 3
            || heavyRare == null || heavyRare.duration() != 60 || heavyRare.amplifier() != 0
            || heavyEsoteric == null || heavyEsoteric.duration() != 280 || heavyEsoteric.amplifier() != 2) {
            throw new IllegalStateException("Aether Zanite Alchemist values did not match the original");
        }
    }

    private static void verifyNaga() {
        verifySocketRule("crafted/naga", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("crafted/naga", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        verifySocketRule("crafted/naga", net.minecraft.world.item.Items.DIAMOND_PICKAXE, false);

        JeicyTieredDropTransformBonus drop = requireBonus(
            gem("crafted/naga"), net.minecraft.world.item.Items.DIAMOND_AXE,
            JeicyTieredDropTransformBonus.class);
        if (!Double.valueOf(0.75).equals(drop.chanceForRank(OriginalGemRank.ARTIFACT))
            || !Double.valueOf(0.9).equals(drop.chanceForRank(OriginalGemRank.HEIRLOOM))
            || !Double.valueOf(1).equals(drop.chanceForRank(OriginalGemRank.ESOTERIC))
            || !drop.acceptsForVerification(new ItemStack(net.minecraft.world.item.Items.OAK_LOG))
            || !drop.outputForVerification().is(item("twilightforest:liveroot"))) {
            throw new IllegalStateException("Naga values did not match the original");
        }
    }

    private static void verifyUndergarden() {
        verifySocketRule("undergarden/decay", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("undergarden/decay", net.minecraft.world.item.Items.DIAMOND_SHOVEL, false);
        verifySocketRule("undergarden/decay", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("undergarden/decay", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("undergarden/decay", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("undergarden/decay", net.minecraft.world.item.Items.DIAMOND_HELMET, false);

        Gem decay = gem("undergarden/decay");
        JeicyTieredDropTransformBonus decayDrop = requireBonus(
            decay, net.minecraft.world.item.Items.DIAMOND_PICKAXE,
            JeicyTieredDropTransformBonus.class);
        JeicyTieredAttributeBonus decayWeapon = requireBonus(
            decay, net.minecraft.world.item.Items.DIAMOND_SWORD,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus decayArmor = requireBonus(
            decay, net.minecraft.world.item.Items.DIAMOND_BOOTS,
            JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(0.15).equals(decayDrop.chanceForRank(OriginalGemRank.RARE))
            || !Double.valueOf(1).equals(decayDrop.chanceForRank(OriginalGemRank.ESOTERIC))
            || !decayDrop.acceptsForVerification(new ItemStack(net.minecraft.world.item.Items.COBBLESTONE))
            || !decayDrop.outputForVerification().is(item("undergarden:depthrock"))
            || !Double.valueOf(0.015).equals(decayWeapon.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.12).equals(decayWeapon.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.05).equals(decayArmor.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.35).equals(decayArmor.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Undergarden Decay values did not match the original");
        }

        verifySocketRule("undergarden/undergarden", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("undergarden/undergarden", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("undergarden/undergarden", net.minecraft.world.item.Items.BOW, true);
        verifySocketRule("undergarden/undergarden", net.minecraft.world.item.Items.CROSSBOW, true);
        verifySocketRule("undergarden/undergarden", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("undergarden/undergarden", net.minecraft.world.item.Items.DIAMOND_SWORD, false);

        Gem undergarden = gem("undergarden/undergarden");
        JeicyTieredAttributeBonus health = requireBonus(
            undergarden, net.minecraft.world.item.Items.DIAMOND_CHESTPLATE,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus lifeSteal = requireBonus(
            undergarden, net.minecraft.world.item.Items.DIAMOND_HELMET,
            JeicyTieredAttributeBonus.class);
        JeicyTieredMobEffectBonus virulence = requireBonus(
            undergarden, net.minecraft.world.item.Items.BOW,
            JeicyTieredMobEffectBonus.class);
        var virulenceMythic = virulence.valueForRank(OriginalGemRank.MYTHIC);
        var virulenceEsoteric = virulence.valueForRank(OriginalGemRank.ESOTERIC);
        if (!Double.valueOf(3).equals(health.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(12).equals(health.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.1).equals(lifeSteal.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.4).equals(lifeSteal.valueForRank(OriginalGemRank.ESOTERIC))
            || virulence.valueForRank(OriginalGemRank.RARE) != null
            || virulenceMythic == null || virulenceMythic.duration() != 180
            || virulenceMythic.amplifier() != 0 || virulenceMythic.cooldown() != 800
            || virulenceEsoteric == null || virulenceEsoteric.duration() != 420
            || virulenceEsoteric.amplifier() != 2 || virulenceEsoteric.cooldown() != 700) {
            throw new IllegalStateException("Undergarden gem values did not match the original");
        }
    }

    private static void verifyHeartland(ServerStartedEvent serverEvent) {
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.DIAMOND_PICKAXE, false);
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.BOW, true);
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.CROSSBOW, true);
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.DIAMOND_SHOVEL, true);
        verifySocketRule("modded/heartland", net.minecraft.world.item.Items.DIAMOND_HELMET, false);

        Gem heartland = gem("modded/heartland");
        JeicyMidasWeaponBonus weapon = requireBonus(
            heartland, net.minecraft.world.item.Items.DIAMOND_AXE,
            JeicyMidasWeaponBonus.class);
        JeicyTieredMobEffectBonus comfort = requireBonus(
            heartland, net.minecraft.world.item.Items.BOW,
            JeicyTieredMobEffectBonus.class);
        JeicyTieredDropTransformBonus shovel = requireBonus(
            heartland, net.minecraft.world.item.Items.DIAMOND_SHOVEL,
            JeicyTieredDropTransformBonus.class);
        var comfortEpic = comfort.valueForRank(OriginalGemRank.EPIC);
        var comfortEsoteric = comfort.valueForRank(OriginalGemRank.ESOTERIC);
        if (weapon.enchantmentValueForRank(OriginalGemRank.EPIC) != 1
            || weapon.enchantmentValueForRank(OriginalGemRank.ESOTERIC) != 3
            || !Double.valueOf(0.05).equals(weapon.transformChanceForRank(OriginalGemRank.EPIC))
            || !Double.valueOf(0.3).equals(weapon.transformChanceForRank(OriginalGemRank.ESOTERIC))
            || !weapon.acceptsForVerification(new ItemStack(net.minecraft.world.item.Items.OAK_LOG))
            || !weapon.outputForVerification().is(item("farmersdelight:tree_bark"))
            || comfortEpic == null || comfortEpic.duration() != 80
            || comfortEpic.amplifier() != 0 || comfortEpic.cooldown() != 900
            || comfortEsoteric == null || comfortEsoteric.duration() != 280
            || comfortEsoteric.amplifier() != 2 || comfortEsoteric.cooldown() != 900
            || !Double.valueOf(0.02).equals(shovel.chanceForRank(OriginalGemRank.EPIC))
            || !Double.valueOf(0.12).equals(shovel.chanceForRank(OriginalGemRank.ESOTERIC))
            || !shovel.acceptsForVerification(new ItemStack(net.minecraft.world.item.Items.DIRT))
            || !shovel.outputForVerification().is(item("farmersdelight:rich_soil"))) {
            throw new IllegalStateException("Heartland values did not match the original");
        }

        var lookup = serverEvent.getServer().registryAccess()
            .lookup(net.minecraft.core.registries.Registries.ENCHANTMENT).orElseThrow();
        var backstabbingKey = net.minecraft.resources.ResourceKey.create(
            net.minecraft.core.registries.Registries.ENCHANTMENT,
            ResourceLocation.parse("farmersdelight:backstabbing"));
        var backstabbing = lookup.getOrThrow(backstabbingKey);
        var enchantments = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(
            net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        ItemStack sword = new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
        ItemStack gemStack = heartland.toStack(Purity.PERFECT);
        JeicyGemRankAccess.assign(gemStack, OriginalGemRank.ARTIFACT);
        var category = dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(sword);
        var instance = dev.shadowsoffire.apotheosis.socket.gem.GemInstance.socketed(
            category, gemStack, 0);
        var event = new net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent(
            sword, enchantments, backstabbing, lookup);
        weapon.getEnchantmentLevels(instance, event);
        if (enchantments.getLevel(backstabbing) != 2) {
            throw new IllegalStateException("Heartland Artifact rank did not add Backstabbing II");
        }
    }

    private static void verifyCataclysmBossGems() {
        verifySocketRule("crafted/harbringer", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("crafted/harbringer", net.minecraft.world.item.Items.DIAMOND_HELMET, false);
        verifySocketRule("crafted/leviathan", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("crafted/leviathan", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, false);
        verifySocketRule("crafted/remnant", net.minecraft.world.item.Items.DIAMOND_LEGGINGS, true);
        verifySocketRule("crafted/remnant", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("crafted/remnant", net.minecraft.world.item.Items.DIAMOND_HELMET, false);

        JeicyTieredMobEffectBonus harbringer = requireBonus(
            gem("crafted/harbringer"), net.minecraft.world.item.Items.DIAMOND_CHESTPLATE,
            JeicyTieredMobEffectBonus.class);
        JeicyTieredMobEffectBonus leviathan = requireBonus(
            gem("crafted/leviathan"), net.minecraft.world.item.Items.DIAMOND_HELMET,
            JeicyTieredMobEffectBonus.class);
        JeicyTieredMobEffectBonus remnant = requireBonus(
            gem("crafted/remnant"), net.minecraft.world.item.Items.DIAMOND_BOOTS,
            JeicyTieredMobEffectBonus.class);
        var harbringerArtifact = harbringer.valueForRank(OriginalGemRank.ARTIFACT);
        var harbringerEsoteric = harbringer.valueForRank(OriginalGemRank.ESOTERIC);
        var leviathanArtifact = leviathan.valueForRank(OriginalGemRank.ARTIFACT);
        var leviathanEsoteric = leviathan.valueForRank(OriginalGemRank.ESOTERIC);
        var remnantArtifact = remnant.valueForRank(OriginalGemRank.ARTIFACT);
        var remnantEsoteric = remnant.valueForRank(OriginalGemRank.ESOTERIC);
        if (harbringerArtifact == null || harbringerArtifact.duration() != 1000
            || harbringerArtifact.amplifier() != 0 || harbringerArtifact.cooldown() != 6000
            || harbringerEsoteric == null || harbringerEsoteric.duration() != 2000
            || harbringerEsoteric.amplifier() != 0 || harbringerEsoteric.cooldown() != 4000
            || leviathanArtifact == null || leviathanArtifact.duration() != 600
            || leviathanArtifact.amplifier() != 0 || leviathanArtifact.cooldown() != 6000
            || leviathanEsoteric == null || leviathanEsoteric.duration() != 900
            || leviathanEsoteric.amplifier() != 0 || leviathanEsoteric.cooldown() != 4000
            || remnantArtifact == null || remnantArtifact.duration() != 100
            || remnantArtifact.amplifier() != 0 || remnantArtifact.cooldown() != 6000
            || remnantEsoteric == null || remnantEsoteric.duration() != 200
            || remnantEsoteric.amplifier() != 0 || remnantEsoteric.cooldown() != 4000) {
            throw new IllegalStateException("Cataclysm boss gem values did not match the original");
        }
    }

    private static void verifyCataclysmGeneralGem() {
        verifySocketRule("modded/cataclysm", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/cataclysm", net.minecraft.world.item.Items.DIAMOND_HELMET, false);
        verifySocketRule("modded/cataclysm", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("modded/cataclysm", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/cataclysm", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("modded/cataclysm", net.minecraft.world.item.Items.DIAMOND_PICKAXE, false);

        Gem cataclysm = gem("modded/cataclysm");
        JeicyTieredMobEffectBonus chest = requireBonus(
            cataclysm, net.minecraft.world.item.Items.DIAMOND_CHESTPLATE,
            JeicyTieredMobEffectBonus.class);
        JeicySplitAttackEffectBonus weapons = requireBonus(
            cataclysm, net.minecraft.world.item.Items.DIAMOND_SWORD,
            JeicySplitAttackEffectBonus.class);
        var chestRare = chest.valueForRank(OriginalGemRank.RARE);
        var chestEsoteric = chest.valueForRank(OriginalGemRank.ESOTERIC);
        var heavyRare = weapons.heavyValueForRank(OriginalGemRank.RARE);
        var heavyEsoteric = weapons.heavyValueForRank(OriginalGemRank.ESOTERIC);
        var lightRare = weapons.lightValueForRank(OriginalGemRank.RARE);
        var lightEsoteric = weapons.lightValueForRank(OriginalGemRank.ESOTERIC);
        if (chestRare == null || chestRare.duration() != 80 || chestRare.amplifier() != 0
            || chestRare.cooldown() != 900
            || chestEsoteric == null || chestEsoteric.duration() != 500
            || chestEsoteric.amplifier() != 2 || chestEsoteric.cooldown() != 900
            || heavyRare == null || heavyRare.duration() != 60 || heavyRare.amplifier() != 0
            || heavyRare.cooldown() != 900
            || heavyEsoteric == null || heavyEsoteric.duration() != 280
            || heavyEsoteric.amplifier() != 2 || heavyEsoteric.cooldown() != 900
            || lightRare == null || lightRare.duration() != 140 || lightRare.amplifier() != 0
            || lightRare.cooldown() != 120
            || lightEsoteric == null || lightEsoteric.duration() != 2000
            || lightEsoteric.amplifier() != 2 || lightEsoteric.cooldown() != 20
            || !weapons.lightStacksOnReapply()
            || weapons.lightStackedAmplifierForVerification(2, OriginalGemRank.ESOTERIC) != 5) {
            throw new IllegalStateException("General Cataclysm gem values did not match the original");
        }
    }

    private static void verifyArsNouveauGems() {
        verifySocketRule("modded/archmage", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("modded/archmage", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/archmage", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("modded/archmage", net.minecraft.world.item.Items.BOW, true);
        verifySocketRule("modded/archmage", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        Gem archmage = gem("modded/archmage");
        JeicyTieredEnchantmentBonus manaRegen = requireBonus(archmage,
            net.minecraft.world.item.Items.DIAMOND_HELMET, JeicyTieredEnchantmentBonus.class);
        JeicyTieredAttributeBonus spellDamage = requireBonus(archmage,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus maxMana = requireBonus(archmage,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredAttributeBonus.class);
        JeicyTieredMobEffectBonus shocked = requireBonus(archmage,
            net.minecraft.world.item.Items.BOW, JeicyTieredMobEffectBonus.class);
        var shockedMythic = shocked.valueForRank(OriginalGemRank.MYTHIC);
        var shockedEsoteric = shocked.valueForRank(OriginalGemRank.ESOTERIC);
        if (manaRegen.valueForRank(OriginalGemRank.RARE) != 1
            || manaRegen.valueForRank(OriginalGemRank.ESOTERIC) != 5
            || !Double.valueOf(0.2).equals(spellDamage.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.8).equals(spellDamage.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.05).equals(maxMana.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.35).equals(maxMana.valueForRank(OriginalGemRank.ESOTERIC))
            || shockedMythic == null || shockedMythic.duration() != 180 || shockedMythic.cooldown() != 500
            || shockedEsoteric == null || shockedEsoteric.duration() != 500
            || shockedEsoteric.amplifier() != 2 || shockedEsoteric.cooldown() != 200) {
            throw new IllegalStateException("Ars Nouveau Archmage values did not match the original");
        }

        verifySocketRule("modded/ars_mana", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("modded/ars_mana", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("modded/ars_mana", net.minecraft.world.item.Items.SHIELD, true);
        verifySocketRule("modded/ars_mana", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, false);
        Gem arsMana = gem("modded/ars_mana");
        JeicyTieredAttributeBonus helmet = requireBonus(arsMana,
            net.minecraft.world.item.Items.DIAMOND_HELMET, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus boots = requireBonus(arsMana,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus shield = requireBonus(arsMana,
            net.minecraft.world.item.Items.SHIELD, JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(25).equals(helmet.valueForRank(OriginalGemRank.COMMON))
            || !Double.valueOf(225).equals(helmet.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.5).equals(boots.valueForRank(OriginalGemRank.COMMON))
            || !Double.valueOf(4.5).equals(boots.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.025).equals(shield.valueForRank(OriginalGemRank.COMMON))
            || !Double.valueOf(0.225).equals(shield.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Ars Mana values did not match the original");
        }
    }

    private static void verifyBeeQueen() {
        verifySocketRule("bumblezone/bee_queen", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("bumblezone/bee_queen", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("bumblezone/bee_queen", net.minecraft.world.item.Items.DIAMOND_AXE, false);
        verifySocketRule("bumblezone/bee_queen", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("bumblezone/bee_queen", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        Gem bee = gem("bumblezone/bee_queen");
        JeicyTieredMobEffectBonus effect = requireBonus(bee,
            net.minecraft.world.item.Items.DIAMOND_SWORD, JeicyTieredMobEffectBonus.class);
        JeicyTieredAttributeBonus fire = requireBonus(bee,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredAttributeBonus.class);
        JeicyTieredMultiAttributeBonus lower = requireBonus(bee,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredMultiAttributeBonus.class);
        var uncommon = effect.valueForRank(OriginalGemRank.UNCOMMON);
        var esoteric = effect.valueForRank(OriginalGemRank.ESOTERIC);
        if (uncommon == null || uncommon.duration() != 100 || uncommon.cooldown() != 100
            || esoteric == null || esoteric.duration() != 1000 || esoteric.amplifier() != 2
            || !effect.stacksOnReapply()
            || effect.stackedAmplifierForVerification(2, OriginalGemRank.ESOTERIC) != 5
            || !Double.valueOf(0.5).equals(fire.valueForRank(OriginalGemRank.UNCOMMON))
            || !Double.valueOf(4).equals(fire.valueForRank(OriginalGemRank.ESOTERIC))
            || lower.profileIndexFor(new ItemStack(net.minecraft.world.item.Items.DIAMOND_BOOTS)) != 0) {
            throw new IllegalStateException("Bee Queen values did not match the original");
        }
    }

    private static void verifyFelfire() {
        verifySocketRule("modded/felfire", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("modded/felfire", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("modded/felfire", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/felfire", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("modded/felfire", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/felfire", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        Gem felfire = gem("modded/felfire");
        JeicyFelfireWeaponBonus weapons = requireBonus(felfire,
            net.minecraft.world.item.Items.DIAMOND_AXE, JeicyFelfireWeaponBonus.class);
        JeicyTieredDropTransformBonus alchemy = requireBonus(felfire,
            net.minecraft.world.item.Items.DIAMOND_PICKAXE, JeicyTieredDropTransformBonus.class);
        JeicyTieredEnchantmentBonus rebounding = requireBonus(felfire,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredEnchantmentBonus.class);
        JeicyTieredAttributeBonus lifeSteal = requireBonus(felfire,
            net.minecraft.world.item.Items.DIAMOND_HELMET, JeicyTieredAttributeBonus.class);
        var flameUncommon = weapons.lightValueForRank(OriginalGemRank.UNCOMMON);
        var flameEsoteric = weapons.lightValueForRank(OriginalGemRank.ESOTERIC);
        if (!Double.valueOf(5.25).equals(weapons.heavyValueForRank(0, OriginalGemRank.ARTIFACT))
            || !Double.valueOf(6.25).equals(weapons.heavyValueForRank(1, OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.05).equals(alchemy.chanceForRank(OriginalGemRank.UNCOMMON))
            || !Double.valueOf(0.4).equals(alchemy.chanceForRank(OriginalGemRank.ESOTERIC))
            || !alchemy.acceptsForVerification(new ItemStack(net.minecraft.world.item.Items.DIAMOND))
            || !alchemy.outputForVerification().is(item("born_in_chaos_v1:pieceofdarkmetal"))
            || flameUncommon == null || flameUncommon.duration() != 100
            || flameEsoteric == null || flameEsoteric.duration() != 600
            || flameEsoteric.amplifier() != 2 || !weapons.lightStacksOnReapply()
            || weapons.lightStackedAmplifierForVerification(2, OriginalGemRank.ESOTERIC) != 5
            || rebounding.valueForRank(OriginalGemRank.RARE) != 1
            || rebounding.valueForRank(OriginalGemRank.ESOTERIC) != 3
            || !Double.valueOf(0.05).equals(lifeSteal.valueForRank(OriginalGemRank.UNCOMMON))
            || !Double.valueOf(0.4).equals(lifeSteal.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Felfire values did not match the original");
        }
    }

    private static void verifyEngineer() {
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.BOW, true);
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.CROSSBOW, true);
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("modded/engineer", net.minecraft.world.item.Items.DIAMOND_LEGGINGS, true);
        Gem engineer = gem("modded/engineer");
        JeicyTieredEnchantmentBonus withering = requireBonus(engineer,
            net.minecraft.world.item.Items.DIAMOND_SWORD, JeicyTieredEnchantmentBonus.class);
        JeicyTieredAttributeBonus entityReach = requireBonus(engineer,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus blockReach = requireBonus(engineer,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredAttributeBonus.class);
        if (withering.valueForRank(OriginalGemRank.RARE) != 1
            || withering.valueForRank(OriginalGemRank.MYTHIC) != 2
            || withering.valueForRank(OriginalGemRank.ESOTERIC) != 3
            || !Double.valueOf(1).equals(entityReach.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(4).equals(entityReach.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(1).equals(blockReach.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(4).equals(blockReach.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Engineer values did not match the original");
        }
    }

    private static void verifyDracula() {
        verifySocketRule("modded/dracula", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("modded/dracula", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/dracula", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("modded/dracula", net.minecraft.world.item.Items.DIAMOND_LEGGINGS, true);
        verifySocketRule("modded/dracula", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/dracula", net.minecraft.world.item.Items.TRIDENT, true);
        Gem dracula = gem("modded/dracula");
        JeicyTieredEnchantmentBonus thorns = requireBonus(dracula,
            net.minecraft.world.item.Items.DIAMOND_HELMET, JeicyTieredEnchantmentBonus.class);
        JeicyTieredMultiAttributeBonus chest = requireBonus(dracula,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredMultiAttributeBonus.class);
        JeicyTieredMultiAttributeBonus lower = requireBonus(dracula,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredMultiAttributeBonus.class);
        JeicyTieredMobEffectBonus sunscreen = requireBonus(dracula,
            net.minecraft.world.item.Items.DIAMOND_SWORD, JeicyTieredMobEffectBonus.class);
        var rare = sunscreen.valueForRank(OriginalGemRank.RARE);
        var esoteric = sunscreen.valueForRank(OriginalGemRank.ESOTERIC);
        if (thorns.valueForRank(OriginalGemRank.RARE) != 1
            || thorns.valueForRank(OriginalGemRank.ESOTERIC) != 3
            || !Double.valueOf(-0.3).equals(chest.valueForRank(0, 0, OriginalGemRank.ARTIFACT))
            || !Double.valueOf(-0.2).equals(chest.valueForRank(0, 1, OriginalGemRank.ESOTERIC))
            || !Double.valueOf(1.2).equals(lower.valueForRank(0, 0, OriginalGemRank.ARTIFACT))
            || !Double.valueOf(0.4).equals(lower.valueForRank(0, 1, OriginalGemRank.ESOTERIC))
            || rare == null || rare.duration() != 300 || rare.cooldown() != 450
            || esoteric == null || esoteric.duration() != 800
            || esoteric.amplifier() != 2 || esoteric.cooldown() != 400) {
            throw new IllegalStateException("Dracula values did not match the original");
        }
    }

    private static void verifyWeepingWell() {
        verifySocketRule("modded/weeping_well", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/weeping_well", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/weeping_well", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("modded/weeping_well", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("modded/weeping_well", net.minecraft.world.item.Items.DIAMOND_LEGGINGS, true);
        verifySocketRule("modded/weeping_well", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        Gem well = gem("modded/weeping_well");
        JeicyTieredAttributeBonus capacity = requireBonus(well,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredAttributeBonus.class);
        JeicyTieredEnchantmentBonus plunder = requireBonus(well,
            net.minecraft.world.item.Items.DIAMOND_SWORD, JeicyTieredEnchantmentBonus.class);
        JeicyTieredAttributeBonus experience = requireBonus(well,
            net.minecraft.world.item.Items.DIAMOND_PICKAXE, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus recovery = requireBonus(well,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus spoils = requireBonus(well,
            net.minecraft.world.item.Items.DIAMOND_HELMET, JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(1).equals(capacity.valueForRank(OriginalGemRank.EPIC))
            || !Double.valueOf(6).equals(capacity.valueForRank(OriginalGemRank.ESOTERIC))
            || plunder.valueForRank(OriginalGemRank.EPIC) != 1
            || plunder.valueForRank(OriginalGemRank.ESOTERIC) != 4
            || !Double.valueOf(0.1).equals(experience.valueForRank(OriginalGemRank.EPIC))
            || !Double.valueOf(1).equals(experience.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.05).equals(recovery.valueForRank(OriginalGemRank.EPIC))
            || !Double.valueOf(0.75).equals(recovery.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.05).equals(spoils.valueForRank(OriginalGemRank.EPIC))
            || !Double.valueOf(0.75).equals(spoils.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Weeping Well values did not match the original");
        }
    }

    private static void verifySoultwisted() {
        verifySocketRule("modded/soultwisted", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/soultwisted", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("modded/soultwisted", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("modded/soultwisted", net.minecraft.world.item.Items.DIAMOND_SWORD, true);
        verifySocketRule("modded/soultwisted", net.minecraft.world.item.Items.TRIDENT, true);
        verifySocketRule("modded/soultwisted", net.minecraft.world.item.Items.DIAMOND_HELMET, true);
        verifySocketRule("modded/soultwisted", net.minecraft.world.item.Items.DIAMOND_LEGGINGS, true);
        Gem soul = gem("modded/soultwisted");
        JeicyTieredMobEffectBonus reinforced = requireBonus(soul,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredMobEffectBonus.class);
        JeicyTieredMobEffectBonus anchored = requireBonus(soul,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredMobEffectBonus.class);
        JeicySplitAttackEffectBonus weapons = requireBonus(soul,
            net.minecraft.world.item.Items.DIAMOND_AXE, JeicySplitAttackEffectBonus.class);
        JeicyTieredAttributeBonus healing = requireBonus(soul,
            net.minecraft.world.item.Items.DIAMOND_HELMET, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus chanting = requireBonus(soul,
            net.minecraft.world.item.Items.DIAMOND_LEGGINGS, JeicyTieredAttributeBonus.class);
        var reinforcedEsoteric = reinforced.valueForRank(OriginalGemRank.ESOTERIC);
        var anchoredHeirloom = anchored.valueForRank(OriginalGemRank.HEIRLOOM);
        var vulnerableEsoteric = weapons.heavyValueForRank(OriginalGemRank.ESOTERIC);
        var chilledEsoteric = weapons.lightValueForRank(OriginalGemRank.ESOTERIC);
        net.minecraft.world.item.Item wandItem = item("eidolon_repraised:bonechill_wand");
        ItemStack wand = new ItemStack(wandItem);
        var wandCategory = dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(wand);
        var wandBonus = soul.getBonus(wandCategory, Purity.PERFECT).orElseThrow();
        if (wandItem == net.minecraft.world.item.Items.AIR
            || wandCategory != JeicyLootCategories.WAND.get()
            || !soul.canApplyTo(wand, soul.toStack(Purity.PERFECT), Purity.PERFECT)
            || !(wandBonus instanceof JeicyTieredAttributeBonus magicPower)
            || !Double.valueOf(0.1).equals(magicPower.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.7).equals(magicPower.valueForRank(OriginalGemRank.ESOTERIC))
            || reinforcedEsoteric == null || reinforcedEsoteric.duration() != 500
            || anchoredHeirloom == null || anchoredHeirloom.duration() != 400
            || anchoredHeirloom.cooldown() != 40
            || vulnerableEsoteric == null || vulnerableEsoteric.duration() != 1000
            || vulnerableEsoteric.amplifier() != 3
            || chilledEsoteric == null || chilledEsoteric.duration() != 1300
            || chilledEsoteric.amplifier() != 2 || !weapons.lightStacksOnReapply()
            || weapons.lightStackedAmplifierForVerification(2, OriginalGemRank.ESOTERIC) != 5
            || !Double.valueOf(0.03).equals(healing.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.25).equals(healing.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.1).equals(chanting.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.7).equals(chanting.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Soultwisted values did not match the original");
        }
    }

    private static void verifyLich() {
        net.minecraft.world.item.Item wand = item("eidolon_repraised:bonechill_wand");
        verifySocketRule("crafted/lich", wand, true);
        verifySocketRule("crafted/lich", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        if (dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(new ItemStack(wand))
                != JeicyLootCategories.WAND.get()) {
            throw new IllegalStateException("Lich wand did not resolve the restored wand category");
        }
        JeicyTieredAttributeBonus magicPower = requireBonus(gem("crafted/lich"), wand,
            JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(1).equals(magicPower.valueForRank(OriginalGemRank.ARTIFACT))
            || !Double.valueOf(1.5).equals(magicPower.valueForRank(OriginalGemRank.HEIRLOOM))
            || !Double.valueOf(2).equals(magicPower.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Lich values did not match the original");
        }
    }

    private static void verifyCuriosGems() {
        net.minecraft.world.item.Item armorItem = item("eidolon_repraised:warded_mail");
        net.minecraft.world.item.Item jewelryItem = item("eidolon_repraised:basic_ring");
        ItemStack armorStack = new ItemStack(armorItem);
        ItemStack jewelryStack = new ItemStack(jewelryItem);
        if (armorItem == net.minecraft.world.item.Items.AIR
            || jewelryItem == net.minecraft.world.item.Items.AIR
            || dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(armorStack)
                != JeicyLootCategories.CURIOS_ARMOR.get()
            || dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(jewelryStack)
                != JeicyLootCategories.CURIOS_JEWELRY.get()) {
            throw new IllegalStateException("Restored Curios categories did not resolve real Eidolon items");
        }

        verifySocketRule("curios/reinforced", armorItem, true);
        verifySocketRule("curios/reinforced", jewelryItem, true);
        verifySocketRule("curios/reinforced", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, false);
        Gem reinforced = gem("curios/reinforced");
        JeicyTieredAttributeBonus reinforcedArmor = requireBonus(reinforced, armorItem,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus reinforcedJewelry = requireBonus(reinforced, jewelryItem,
            JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(0.5).equals(reinforcedArmor.valueForRank(OriginalGemRank.COMMON))
            || !Double.valueOf(5).equals(reinforcedArmor.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.5).equals(reinforcedJewelry.valueForRank(OriginalGemRank.COMMON))
            || !Double.valueOf(5).equals(reinforcedJewelry.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Reinforced values did not match the original");
        }

        verifySocketRule("curios/twisted", armorItem, true);
        verifySocketRule("curios/twisted", jewelryItem, true);
        Gem twisted = gem("curios/twisted");
        JeicyTieredMultiAttributeBonus twistedArmor = requireBonus(twisted, armorItem,
            JeicyTieredMultiAttributeBonus.class);
        JeicyTieredMultiAttributeBonus twistedJewelry = requireBonus(twisted, jewelryItem,
            JeicyTieredMultiAttributeBonus.class);
        if (!Double.valueOf(3).equals(twistedArmor.valueForRank(0, 0, OriginalGemRank.ARTIFACT))
            || !Double.valueOf(-4).equals(twistedArmor.valueForRank(0, 1, OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.06).equals(twistedJewelry.valueForRank(0, 0, OriginalGemRank.ARTIFACT))
            || !Double.valueOf(-0.08).equals(twistedJewelry.valueForRank(0, 1, OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Twisted values did not match the original");
        }

        verifySocketRule("curios/vitalic", armorItem, true);
        verifySocketRule("curios/vitalic", jewelryItem, true);
        Gem vitalic = gem("curios/vitalic");
        JeicyTieredAttributeBonus vitalicArmor = requireBonus(vitalic, armorItem,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus vitalicJewelry = requireBonus(vitalic, jewelryItem,
            JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(0.5).equals(vitalicArmor.valueForRank(OriginalGemRank.COMMON))
            || !Double.valueOf(5).equals(vitalicArmor.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(5).equals(vitalicJewelry.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Vitalic values did not match the original");
        }
    }

    private static void verifyUmbralMatrimony() {
        net.minecraft.world.item.Item armorItem = item("eidolon_repraised:warded_mail");
        net.minecraft.world.item.Item jewelryItem = item("eidolon_repraised:basic_ring");
        net.minecraft.world.item.Item aetherItem = item("aether:iron_ring");
        verifySocketRule("twilight/umbral_matrimony", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("twilight/umbral_matrimony", armorItem, true);
        verifySocketRule("twilight/umbral_matrimony", jewelryItem, true);
        verifySocketRule("twilight/umbral_matrimony", aetherItem, true);
        if (aetherItem == net.minecraft.world.item.Items.AIR
            || dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(new ItemStack(aetherItem))
                != JeicyLootCategories.AETHER_ACCESSORY.get()) {
            throw new IllegalStateException("Restored Aether accessory category did not resolve iron_ring");
        }

        Gem umbral = gem("twilight/umbral_matrimony");
        JeicyTieredDropTransformBonus alchemy = requireBonus(umbral,
            net.minecraft.world.item.Items.DIAMOND_PICKAXE, JeicyTieredDropTransformBonus.class);
        JeicyTieredAttributeBonus armor = requireBonus(umbral, armorItem,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus jewelry = requireBonus(umbral, jewelryItem,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus aether = requireBonus(umbral, aetherItem,
            JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(0.05).equals(alchemy.chanceForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.35).equals(alchemy.chanceForRank(OriginalGemRank.ESOTERIC))
            || !alchemy.acceptsForVerification(new ItemStack(item("aether:zanite_gemstone")))
            || !alchemy.outputForVerification().is(item("twilightforest:liveroot"))
            || !Double.valueOf(0.5).equals(armor.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(3.5).equals(armor.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.5).equals(jewelry.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(3.5).equals(jewelry.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.25).equals(aether.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(2).equals(aether.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Umbral Matrimony values did not match the original");
        }
    }

    private static void verifyIronSpellbookGems() {
        net.minecraft.world.item.Item wand = item("eidolon_repraised:bonechill_wand");
        net.minecraft.world.item.Item spellbook = item("irons_spellbooks:iron_spell_book");
        if (spellbook == net.minecraft.world.item.Items.AIR
            || dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(new ItemStack(spellbook))
                != JeicyLootCategories.SPELLBOOK.get()) {
            throw new IllegalStateException("Restored spellbook category did not resolve iron_spell_book");
        }

        verifySocketRule("modded/bloodcaster", wand, true);
        verifySocketRule("modded/bloodcaster", spellbook, true);
        verifySocketRule("modded/bloodcaster", net.minecraft.world.item.Items.DIAMOND_BOOTS, true);
        verifySocketRule("modded/bloodcaster", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        Gem bloodcaster = gem("modded/bloodcaster");
        JeicyTieredMobEffectBonus vigor = requireBonus(bloodcaster, wand,
            JeicyTieredMobEffectBonus.class);
        JeicyTieredAttributeBonus bloodPower = requireBonus(bloodcaster, spellbook,
            JeicyTieredAttributeBonus.class);
        JeicyTieredMultiAttributeBonus bloodResist = requireBonus(bloodcaster,
            net.minecraft.world.item.Items.DIAMOND_BOOTS, JeicyTieredMultiAttributeBonus.class);
        var vigorEpic = vigor.valueForRank(OriginalGemRank.EPIC);
        var vigorEsoteric = vigor.valueForRank(OriginalGemRank.ESOTERIC);
        if (vigorEpic == null || vigorEpic.duration() != 60 || vigorEpic.cooldown() != 400
            || vigorEsoteric == null || vigorEsoteric.duration() != 500
            || vigorEsoteric.amplifier() != 1 || vigorEsoteric.cooldown() != 400
            || !Double.valueOf(0.5).equals(bloodPower.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(3.5).equals(bloodPower.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.55).equals(bloodResist.valueForRank(0, 0, OriginalGemRank.ARTIFACT))
            || !Double.valueOf(-0.7).equals(bloodResist.valueForRank(0, 1, OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Bloodcaster values did not match the original");
        }

        verifySocketRule("modded/mystic", wand, true);
        verifySocketRule("modded/mystic", spellbook, true);
        verifySocketRule("modded/mystic", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("modded/mystic", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        Gem mystic = gem("modded/mystic");
        JeicyTieredMobEffectBonus reinforced = requireBonus(mystic, wand,
            JeicyTieredMobEffectBonus.class);
        JeicyTieredAttributeBonus mana = requireBonus(mystic, spellbook,
            JeicyTieredAttributeBonus.class);
        JeicyTieredDropTransformBonus alchemy = requireBonus(mystic,
            net.minecraft.world.item.Items.DIAMOND_PICKAXE, JeicyTieredDropTransformBonus.class);
        var reinforcedEsoteric = reinforced.valueForRank(OriginalGemRank.ESOTERIC);
        net.minecraft.world.item.Item rawSilver = item("eidolon_repraised:raw_silver");
        if (reinforcedEsoteric == null || reinforcedEsoteric.duration() != 500
            || reinforcedEsoteric.amplifier() != 0 || reinforcedEsoteric.cooldown() != 400
            || !Double.valueOf(50).equals(mana.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(250).equals(mana.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.1).equals(alchemy.chanceForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.7).equals(alchemy.chanceForRank(OriginalGemRank.ESOTERIC))
            || rawSilver == net.minecraft.world.item.Items.AIR
            || !alchemy.acceptsForVerification(new ItemStack(rawSilver))
            || !alchemy.outputForVerification().is(item("irons_spellbooks:arcane_essence"))) {
            throw new IllegalStateException("Mystic values did not match the original");
        }
    }

    private static void verifyHive() {
        net.minecraft.world.item.Item aetherItem = item("aether:iron_ring");
        net.minecraft.world.item.Item spellbook = item("irons_spellbooks:iron_spell_book");
        net.minecraft.world.item.Item jewelryItem = item("eidolon_repraised:basic_ring");
        net.minecraft.world.item.Item armorItem = item("eidolon_repraised:warded_mail");
        verifySocketRule("bumblezone/hive", aetherItem, true);
        verifySocketRule("bumblezone/hive", spellbook, true);
        verifySocketRule("bumblezone/hive", jewelryItem, true);
        verifySocketRule("bumblezone/hive", armorItem, true);
        verifySocketRule("bumblezone/hive", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, false);
        Gem hive = gem("bumblezone/hive");
        JeicyTieredAttributeBonus arrows = requireBonus(hive, aetherItem,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus spellPower = requireBonus(hive, spellbook,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus jewelry = requireBonus(hive, jewelryItem,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus armor = requireBonus(hive, armorItem,
            JeicyTieredAttributeBonus.class);
        if (!Double.valueOf(0.015).equals(arrows.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.105).equals(arrows.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.5).equals(spellPower.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(3.5).equals(spellPower.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.01).equals(jewelry.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.07).equals(jewelry.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.01).equals(armor.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.07).equals(armor.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Hive values did not match the original");
        }
    }

    private static void verifySculk() {
        net.minecraft.world.item.Item armorItem = item("eidolon_repraised:warded_mail");
        net.minecraft.world.item.Item aetherItem = item("aether:iron_ring");
        verifySocketRule("otherside/sculk", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);
        verifySocketRule("otherside/sculk", armorItem, true);
        verifySocketRule("otherside/sculk", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("otherside/sculk", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        verifySocketRule("otherside/sculk", aetherItem, true);

        Gem sculk = gem("otherside/sculk");
        JeicyTieredDropTransformBonus alchemy = requireBonus(sculk,
            net.minecraft.world.item.Items.DIAMOND_PICKAXE, JeicyTieredDropTransformBonus.class);
        JeicyTieredAttributeBonus armor = requireBonus(sculk, armorItem,
            JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus heavy = requireBonus(sculk,
            net.minecraft.world.item.Items.DIAMOND_AXE, JeicyTieredAttributeBonus.class);
        JeicyTieredAttributeBonus aether = requireBonus(sculk, aetherItem,
            JeicyTieredAttributeBonus.class);
        net.minecraft.world.item.Item gravitite = item("aether:gravitite_ore");
        if (!Double.valueOf(0.01).equals(alchemy.chanceForRank(OriginalGemRank.MYTHIC))
            || !Double.valueOf(0.05).equals(alchemy.chanceForRank(OriginalGemRank.ESOTERIC))
            || gravitite == net.minecraft.world.item.Items.AIR
            || !alchemy.acceptsForVerification(new ItemStack(gravitite))
            || !alchemy.outputForVerification().is(item("deeperdarker:warden_carapace"))
            || !Double.valueOf(0.25).equals(armor.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(1.75).equals(armor.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.01).equals(heavy.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(0.1).equals(heavy.valueForRank(OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.25).equals(aether.valueForRank(OriginalGemRank.RARE))
            || !Double.valueOf(2).equals(aether.valueForRank(OriginalGemRank.ESOTERIC))) {
            throw new IllegalStateException("Sculk values did not match the original");
        }
    }

    private static void verifyWitchQueen() {
        net.minecraft.world.item.Item wand = item("eidolon_repraised:bonechill_wand");
        verifySocketRule("modded/witch_queen", wand, true);
        verifySocketRule("modded/witch_queen", net.minecraft.world.item.Items.DIAMOND_AXE, true);
        verifySocketRule("modded/witch_queen", net.minecraft.world.item.Items.DIAMOND_SWORD, false);
        verifySocketRule("modded/witch_queen", net.minecraft.world.item.Items.BOW, true);
        verifySocketRule("modded/witch_queen", net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, true);
        verifySocketRule("modded/witch_queen", net.minecraft.world.item.Items.DIAMOND_LEGGINGS, true);
        verifySocketRule("modded/witch_queen", net.minecraft.world.item.Items.DIAMOND_PICKAXE, true);

        Gem witch = gem("modded/witch_queen");
        JeicyTieredMobEffectBonus anchored = requireBonus(witch, wand,
            JeicyTieredMobEffectBonus.class);
        JeicyTieredMobEffectBonus grievous = requireBonus(witch,
            net.minecraft.world.item.Items.DIAMOND_AXE, JeicyTieredMobEffectBonus.class);
        JeicyTieredMultiAttributeBonus ranged = requireBonus(witch,
            net.minecraft.world.item.Items.BOW, JeicyTieredMultiAttributeBonus.class);
        JeicyTieredAttributeBonus chanting = requireBonus(witch,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, JeicyTieredAttributeBonus.class);
        JeicyTieredMultiAttributeBonus leggings = requireBonus(witch,
            net.minecraft.world.item.Items.DIAMOND_LEGGINGS, JeicyTieredMultiAttributeBonus.class);
        JeicyTieredDropTransformBonus alchemy = requireBonus(witch,
            net.minecraft.world.item.Items.DIAMOND_PICKAXE, JeicyTieredDropTransformBonus.class);
        var anchoredUncommon = anchored.valueForRank(OriginalGemRank.UNCOMMON);
        var anchoredEsoteric = anchored.valueForRank(OriginalGemRank.ESOTERIC);
        var grievousEsoteric = grievous.valueForRank(OriginalGemRank.ESOTERIC);
        net.minecraft.world.item.Item moonCobble = item("ad_astra:moon_cobblestone");
        if (anchoredUncommon == null || anchoredUncommon.duration() != 30
            || anchoredUncommon.amplifier() != 0 || anchoredUncommon.cooldown() != 3000
            || anchoredEsoteric == null || anchoredEsoteric.duration() != 300
            || anchoredEsoteric.amplifier() != 1 || anchoredEsoteric.cooldown() != 1000
            || grievousEsoteric == null || grievousEsoteric.duration() != 1000
            || grievousEsoteric.amplifier() != 2 || grievousEsoteric.cooldown() != 1000
            || !Double.valueOf(1).equals(ranged.valueForRank(0, 0, OriginalGemRank.UNCOMMON))
            || !Double.valueOf(8).equals(ranged.valueForRank(0, 1, OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.15).equals(chanting.valueForRank(OriginalGemRank.UNCOMMON))
            || !Double.valueOf(2).equals(chanting.valueForRank(OriginalGemRank.HEIRLOOM))
            || chanting.valueForRank(OriginalGemRank.ESOTERIC) != null
            || !Double.valueOf(0.05).equals(leggings.valueForRank(0, 0, OriginalGemRank.UNCOMMON))
            || !Double.valueOf(0.7).equals(leggings.valueForRank(0, 1, OriginalGemRank.ESOTERIC))
            || !Double.valueOf(0.01).equals(alchemy.chanceForRank(OriginalGemRank.UNCOMMON))
            || !Double.valueOf(0.21).equals(alchemy.chanceForRank(OriginalGemRank.ESOTERIC))
            || moonCobble == net.minecraft.world.item.Items.AIR
            || !alchemy.acceptsForVerification(new ItemStack(moonCobble))
            || !alchemy.outputForVerification().is(item("apotheosis:common_material"))) {
            throw new IllegalStateException("Witch Queen values did not match the original");
        }
    }

    private static Gem gem(String path) {
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", path));
        if (gem == null) throw new IllegalStateException("Missing gem: " + path);
        return gem;
    }

    private static net.minecraft.world.item.Item item(String id) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
            ResourceLocation.parse(id));
    }

    private static <T> T requireBonus(Gem gem, net.minecraft.world.item.Item target,
                                      Class<T> expectedType) {
        ItemStack stack = new ItemStack(target);
        var bonus = gem.getBonus(dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(stack),
            Purity.PERFECT).orElseThrow();
        if (!expectedType.isInstance(bonus)) {
            throw new IllegalStateException("Expected " + expectedType.getSimpleName()
                + " for " + target + ", got " + bonus.getClass().getSimpleName());
        }
        return expectedType.cast(bonus);
    }

    private static void verifyEnderDragonEnchantments(ServerStartedEvent serverEvent) {
        var lookup = serverEvent.getServer().registryAccess()
            .lookup(net.minecraft.core.registries.Registries.ENCHANTMENT).orElseThrow();
        var sharpness = lookup.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SHARPNESS);
        var unbreaking = lookup.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING);
        var enchantments = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(
            net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        enchantments.set(sharpness, 3);
        enchantments.set(unbreaking, 2);

        ItemStack targetStack = new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
        Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "crafted/ender_dragon"));
        ItemStack gemStack = gem.toStack(Purity.PERFECT);
        var category = dev.shadowsoffire.apotheosis.loot.LootCategory.forItem(targetStack);
        var instance = dev.shadowsoffire.apotheosis.socket.gem.GemInstance.socketed(
            category, gemStack, 0);
        var bonus = gem.getBonus(category, Purity.PERFECT).orElseThrow();
        var levelEvent = new net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent(
            targetStack, enchantments, sharpness, lookup);
        bonus.getEnchantmentLevels(instance, levelEvent);

        if (enchantments.getLevel(sharpness) != 5 || enchantments.getLevel(unbreaking) != 4) {
            throw new IllegalStateException("Ender Dragon global enchantment bonus did not preserve +2");
        }
    }

    private static void verifyOriginalRankEvolution(ServerStartedEvent serverEvent) {
        var level = serverEvent.getServer().overworld();
        var recipes = serverEvent.getServer().getRecipeManager();
        var registries = serverEvent.getServer().registryAccess();

        Gem armor = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "modded/armor"));
        ItemStack common = armor.toStack(Purity.CRACKED);
        var commonInput = cuttingInput(common, common.copy(),
            new ItemStack(dev.shadowsoffire.apotheosis.Apoth.Items.GEM_DUST.value(), 1),
            new ItemStack(dev.shadowsoffire.apotheosis.Apoth.Items.TIMEWORN_FABRIC.value(), 1));
        var nativeRecipe = recipes.getRecipeFor(
            dev.shadowsoffire.apotheosis.Apoth.RecipeTypes.GEM_CUTTING, commonInput, level)
            .orElseThrow(() -> new IllegalStateException("Common original gem cutting recipe not found"));
        ItemStack uncommon = nativeRecipe.value().assemble(commonInput, registries);
        if (JeicyGemRankAccess.get(uncommon) != OriginalGemRank.UNCOMMON
            || dev.shadowsoffire.apotheosis.socket.gem.GemItem.getPurity(uncommon) != Purity.CHIPPED) {
            throw new IllegalStateException("Native cutting did not advance Common to Uncommon");
        }

        ItemStack epic = armor.toStack(Purity.NORMAL);
        var forbiddenInput = cuttingInput(epic, epic.copy(),
            new ItemStack(dev.shadowsoffire.apotheosis.Apoth.Items.GEM_DUST.value(), 7),
            new ItemStack(dev.shadowsoffire.apotheosis.Apoth.Items.TIMEWORN_FABRIC.value(), 27));
        if (recipes.getRecipeFor(dev.shadowsoffire.apotheosis.Apoth.RecipeTypes.GEM_CUTTING,
            forbiddenInput, level).isPresent()) {
            throw new IllegalStateException("Original cutting accepted a material two ranks below");
        }

        ItemStack mythic = armor.toStack(Purity.FLAWLESS);
        var ancientInput = cuttingInput(mythic, mythic.copy(),
            new ItemStack(dev.shadowsoffire.apotheosis.Apoth.Items.GEM_DUST.value(), 9),
            new ItemStack(JeicyApotheoticAddition.ANCIENT_MATERIAL.get(), 1));
        var ancientRecipe = recipes.getRecipeFor(
            dev.shadowsoffire.apotheosis.Apoth.RecipeTypes.GEM_CUTTING, ancientInput, level)
            .orElseThrow(() -> new IllegalStateException("Mythic to Ancient cutting recipe not found"));
        ItemStack ancient = ancientRecipe.value().assemble(ancientInput, registries);
        if (JeicyGemRankAccess.get(ancient) != OriginalGemRank.ANCIENT
            || dev.shadowsoffire.apotheosis.socket.gem.GemItem.getPurity(ancient) != Purity.PERFECT) {
            throw new IllegalStateException("Custom cutting did not advance Mythic to Ancient");
        }

        Gem dragon = GemRegistry.INSTANCE.getValue(ResourceLocation.fromNamespaceAndPath(
            "apotheotic_additions", "crafted/ender_dragon"));
        ItemStack artifact = dragon.toStack(Purity.PERFECT);
        var heirloomInput = cuttingInput(artifact, artifact.copy(),
            new ItemStack(dev.shadowsoffire.apotheosis.Apoth.Items.GEM_DUST.value(), 13),
            new ItemStack(JeicyApotheoticAddition.HEIRLOOM_MATERIAL.get(), 1));
        var heirloomRecipe = recipes.getRecipeFor(
            dev.shadowsoffire.apotheosis.Apoth.RecipeTypes.GEM_CUTTING, heirloomInput, level)
            .orElseThrow(() -> new IllegalStateException("Artifact to Heirloom recipe not found"));
        ItemStack heirloom = heirloomRecipe.value().assemble(heirloomInput, registries);
        if (JeicyGemRankAccess.get(heirloom) != OriginalGemRank.HEIRLOOM) {
            throw new IllegalStateException("Custom cutting did not advance Artifact to Heirloom");
        }

        var esotericInput = cuttingInput(heirloom, heirloom.copy(),
            new ItemStack(dev.shadowsoffire.apotheosis.Apoth.Items.GEM_DUST.value(), 15),
            new ItemStack(JeicyApotheoticAddition.ESOTERIC_MATERIAL.get(), 1));
        var esotericRecipe = recipes.getRecipeFor(
            dev.shadowsoffire.apotheosis.Apoth.RecipeTypes.GEM_CUTTING, esotericInput, level)
            .orElseThrow(() -> new IllegalStateException("Heirloom to Esoteric recipe not found"));
        ItemStack esoteric = esotericRecipe.value().assemble(esotericInput, registries);
        if (JeicyGemRankAccess.get(esoteric) != OriginalGemRank.ESOTERIC) {
            throw new IllegalStateException("Custom cutting did not advance Heirloom to Esoteric");
        }
    }

    private static dev.shadowsoffire.apotheosis.socket.gem.cutting.GemCuttingRecipe.CuttingRecipeInput
        cuttingInput(ItemStack base, ItemStack top, ItemStack left, ItemStack right) {
        var handler = new net.neoforged.neoforge.items.ItemStackHandler(4);
        handler.setStackInSlot(0, base);
        handler.setStackInSlot(1, top);
        handler.setStackInSlot(2, left);
        handler.setStackInSlot(3, right);
        return new dev.shadowsoffire.apotheosis.socket.gem.cutting.GemCuttingRecipe.CuttingRecipeInput(
            handler);
    }
}
