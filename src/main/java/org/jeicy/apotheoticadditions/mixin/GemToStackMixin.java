package org.jeicy.apotheoticadditions.mixin;

import org.jeicy.apotheoticadditions.JeicyGemRankAccess;
import org.jeicy.apotheoticadditions.OriginalGemCatalog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.shadowsoffire.apotheosis.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Assigns the independent original rarity whenever Apotheosis creates our gems. */
@Mixin(Gem.class)
public abstract class GemToStackMixin {
    @Inject(method = "toStack", at = @At("RETURN"))
    private void apotheoticAdditions$assignOriginalRank(
        Purity purity, CallbackInfoReturnable<ItemStack> callback) {
        Gem gem = (Gem) (Object) this;
        ResourceLocation id = gem.getId();
        if (id != null && "apotheotic_additions".equals(id.getNamespace())) {
            JeicyGemRankAccess.assign(callback.getReturnValue(),
                OriginalGemCatalog.initialRank(id.getPath(), purity.ordinal()));
        }
    }
}
