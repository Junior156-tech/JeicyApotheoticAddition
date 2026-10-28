package org.jeicy.apotheoticadditions;

import com.mojang.serialization.MapCodec;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.cutting.GemCuttingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** Exact nine-rank successor to Apotheosis 1.20's RarityUpgrade cutting recipe. */
public final class OriginalRankUpgradeRecipe implements GemCuttingRecipe {
    public static final OriginalRankUpgradeRecipe INSTANCE = new OriginalRankUpgradeRecipe();

    private OriginalRankUpgradeRecipe() {}

    @Override
    public boolean matches(CuttingRecipeInput input, Level level) {
        if (!matchesOriginalInputs(input)) return false;
        ItemStack base = input.getBase();
        OriginalGemRank rank = JeicyGemRankAccess.get(base);
        int materialRank = materialRank(input.getRight());
        return usesCustomPath(base, rank, materialRank);
    }

    @Override
    public ItemStack assemble(CuttingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack output = input.getBase().copy();
        output.setCount(1);
        if (GemItem.getPurity(output) != Purity.PERFECT) {
            GemItem.setPurity(output, GemItem.getPurity(output).next());
        }
        JeicyGemRankAccess.advanceOne(output);
        return output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JeicyApotheoticAddition.ORIGINAL_RANK_UPGRADE_SERIALIZER.get();
    }

    @Override
    public void decrementInputs(CuttingRecipeInput input, Level level) {
        OriginalGemRank rank = JeicyGemRankAccess.get(input.getBase());
        int materialCost = OriginalGemUpgradeCosts.materialCost(
            rank.stableId(), materialRank(input.getRight()));
        input.getBase().shrink(1);
        input.getTop().shrink(1);
        input.getLeft().shrink(OriginalGemUpgradeCosts.dustCost(rank));
        input.getRight().shrink(materialCost);
    }

    @Override
    public boolean isValidBaseItem(CuttingRecipeInput input, ItemStack stack) {
        OriginalGemRank rank = JeicyGemRankAccess.get(stack);
        if (rank == null || !rank.canAdvance()) return false;
        Purity purity = GemItem.getPurity(stack);
        return rank == OriginalGemRank.MYTHIC && purity == Purity.FLAWLESS
            || rank.isAtLeast(OriginalGemRank.ANCIENT) && purity == Purity.PERFECT;
    }

    @Override
    public boolean isValidTopItem(CuttingRecipeInput input, ItemStack stack) {
        return sameGemAndRank(input.getBase(), stack);
    }

    @Override
    public boolean isValidLeftItem(CuttingRecipeInput input, ItemStack stack) {
        return stack.is(Apoth.Items.GEM_DUST.value());
    }

    @Override
    public boolean isValidRightItem(CuttingRecipeInput input, ItemStack stack) {
        OriginalGemRank rank = JeicyGemRankAccess.get(input.getBase());
        if (rank == null) return false;
        int materialRank = materialRank(stack);
        return OriginalGemUpgradeCosts.materialCost(rank.stableId(), materialRank) > 0
            && usesCustomPath(input.getBase(), rank, materialRank);
    }

    public static boolean matchesOriginalInputs(CuttingRecipeInput input) {
        ItemStack base = input.getBase();
        OriginalGemRank rank = JeicyGemRankAccess.get(base);
        if (rank == null || !rank.canAdvance() || !sameGemAndRank(base, input.getTop())) return false;
        if (!input.getLeft().is(Apoth.Items.GEM_DUST.value())
            || input.getLeft().getCount() < OriginalGemUpgradeCosts.dustCost(rank)) return false;
        int materialCost = OriginalGemUpgradeCosts.materialCost(
            rank.stableId(), materialRank(input.getRight()));
        return materialCost > 0 && input.getRight().getCount() >= materialCost;
    }

    public static boolean isValidOriginalMaterial(CuttingRecipeInput input, ItemStack material) {
        OriginalGemRank rank = JeicyGemRankAccess.get(input.getBase());
        return rank != null && OriginalGemUpgradeCosts.materialCost(
            rank.stableId(), materialRank(material)) > 0;
    }

    private static boolean usesCustomPath(ItemStack base, OriginalGemRank rank, int materialRank) {
        Purity purity = GemItem.getPurity(base);
        if (rank == OriginalGemRank.MYTHIC && purity == Purity.FLAWLESS) {
            return materialRank == OriginalGemRank.ANCIENT.stableId();
        }
        return rank.isAtLeast(OriginalGemRank.ANCIENT) && purity == Purity.PERFECT;
    }

    private static boolean sameGemAndRank(ItemStack first, ItemStack second) {
        OriginalGemRank firstRank = JeicyGemRankAccess.get(first);
        OriginalGemRank secondRank = JeicyGemRankAccess.get(second);
        return firstRank != null && firstRank == secondRank
            && GemItem.getGem(first) == GemItem.getGem(second);
    }

    private static int materialRank(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Apoth.Items.MYSTERIOUS_SCRAP_METAL.value()) return OriginalGemRank.COMMON.stableId();
        if (item == Apoth.Items.TIMEWORN_FABRIC.value()) return OriginalGemRank.UNCOMMON.stableId();
        if (item == Apoth.Items.LUMINOUS_CRYSTAL_SHARD.value()) return OriginalGemRank.RARE.stableId();
        if (item == Apoth.Items.ARCANE_SANDS.value()) return OriginalGemRank.EPIC.stableId();
        if (item == Apoth.Items.GODFORGED_PEARL.value()) return OriginalGemRank.MYTHIC.stableId();
        if (item == JeicyApotheoticAddition.ANCIENT_MATERIAL.get()) return OriginalGemRank.ANCIENT.stableId();
        if (item == JeicyApotheoticAddition.ARTIFACT_MATERIAL.get()) return OriginalGemRank.ARTIFACT.stableId();
        if (item == JeicyApotheoticAddition.HEIRLOOM_MATERIAL.get()) return OriginalGemRank.HEIRLOOM.stableId();
        if (item == JeicyApotheoticAddition.ESOTERIC_MATERIAL.get()) return OriginalGemRank.ESOTERIC.stableId();
        return -100;
    }

    public static final class Serializer implements RecipeSerializer<OriginalRankUpgradeRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<OriginalRankUpgradeRecipe> CODEC =
            MapCodec.unit(OriginalRankUpgradeRecipe.INSTANCE);
        private static final StreamCodec<RegistryFriendlyByteBuf, OriginalRankUpgradeRecipe> STREAM_CODEC =
            StreamCodec.unit(OriginalRankUpgradeRecipe.INSTANCE);

        private Serializer() {}

        @Override
        public MapCodec<OriginalRankUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OriginalRankUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
