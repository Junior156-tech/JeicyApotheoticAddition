package org.jeicy.apotheoticadditions.mixin;

import org.jeicy.apotheoticadditions.JeicyGemRankAccess;
import org.jeicy.apotheoticadditions.OriginalGemRank;
import org.jeicy.apotheoticadditions.OriginalRankUpgradeRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.shadowsoffire.apotheosis.socket.gem.cutting.GemCuttingRecipe.CuttingRecipeInput;
import dev.shadowsoffire.apotheosis.socket.gem.cutting.PurityUpgradeRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Keeps native purity cutting synchronized with the first six original ranks. */
@Mixin(PurityUpgradeRecipe.class)
public abstract class PurityUpgradeRecipeMixin {
    @Inject(
        method = "matches(Ldev/shadowsoffire/apotheosis/socket/gem/cutting/GemCuttingRecipe$CuttingRecipeInput;Lnet/minecraft/world/level/Level;)Z",
        at = @At("RETURN"),
        cancellable = true)
    private void apotheoticAdditions$validateOriginalCosts(
        CuttingRecipeInput input, Level level, CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue() && JeicyGemRankAccess.get(input.getBase()) != null
            && !OriginalRankUpgradeRecipe.matchesOriginalInputs(input)) {
            callback.setReturnValue(false);
        }
    }

    @Inject(
        method = "isValidRightItem(Ldev/shadowsoffire/apotheosis/socket/gem/cutting/GemCuttingRecipe$CuttingRecipeInput;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At("RETURN"),
        cancellable = true)
    private void apotheoticAdditions$validateOriginalMaterialSlot(
        CuttingRecipeInput input, ItemStack candidate,
        CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue() && JeicyGemRankAccess.get(input.getBase()) != null
            && !OriginalRankUpgradeRecipe.isValidOriginalMaterial(input, candidate)) {
            callback.setReturnValue(false);
        }
    }

    @Inject(
        method = "assemble(Ldev/shadowsoffire/apotheosis/socket/gem/cutting/GemCuttingRecipe$CuttingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"))
    private void apotheoticAdditions$advanceOriginalRank(
        CuttingRecipeInput input, HolderLookup.Provider registries,
        CallbackInfoReturnable<ItemStack> callback) {
        OriginalGemRank rank = JeicyGemRankAccess.get(input.getBase());
        ItemStack output = callback.getReturnValue();
        if (rank != null && rank.canAdvance() && !output.isEmpty()) {
            JeicyGemRankAccess.advanceOne(output);
        }
    }
}
