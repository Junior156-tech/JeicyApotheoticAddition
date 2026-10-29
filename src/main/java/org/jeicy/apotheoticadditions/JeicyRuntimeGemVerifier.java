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
        Map<String, String> conditionalGems = Map.of(
            "aether/aether", "aether",
            "aether/purity", "aether",
            "aether/zanite_alchemist", "aether",
            "crafted/harbringer", "cataclysm",
            "crafted/leviathan", "cataclysm",
            "crafted/remnant", "cataclysm",
            "crafted/sunbird", "mowziesmobs");
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
