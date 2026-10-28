package org.jeicy.apotheoticadditions;

import net.minecraft.world.item.ItemStack;

/** Bonus contract used to restore the pre-1.21 loot-category restrictions. */
public interface JeicyExactSocketBonus {
    boolean matchesSocket(ItemStack socketed);
}
