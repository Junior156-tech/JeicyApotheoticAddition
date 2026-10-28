package org.jeicy.apotheoticadditions.mixin;

import org.jeicy.apotheoticadditions.JeicyExactSocketBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Restores exact 7.x socket restrictions after 8.x merged loot categories. */
@Mixin(Gem.class)
public abstract class GemSocketValidationMixin {
    @Inject(method = "isValidIn", at = @At("RETURN"), cancellable = true)
    private void apotheoticAdditions$validateOriginalCategory(
        ItemStack socketed, ItemStack gemStack, Purity purity,
        CallbackInfoReturnable<Boolean> callback) {
        if (!callback.getReturnValue()) return;

        Gem gem = (Gem) (Object) this;
        ResourceLocation id = gem.getId();
        if (id == null || !"apotheotic_additions".equals(id.getNamespace())) return;

        LootCategory category = LootCategory.forItem(socketed);
        GemBonus bonus = gem.getBonus(category, purity).orElse(null);
        if (bonus instanceof JeicyExactSocketBonus exact && !exact.matchesSocket(socketed)) {
            callback.setReturnValue(false);
        }
    }
}
