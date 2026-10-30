package org.jeicy.apotheoticadditions;

import java.util.Collection;

import dev.shadowsoffire.apotheosis.loot.LootCategory;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.neoforged.neoforge.common.ItemAbilities;

/**
 * Runtime equivalents of Apotheosis 7.x's original LootCategory predicates.
 * Audited against the official Apotheosis 1.20 branch LootCategory and
 * ShieldBreakerTest implementations.
 */
public final class OriginalSocketMatcher {
    private OriginalSocketMatcher() {}

    public static boolean matchesAny(ItemStack stack, Collection<String> types) {
        return types.stream().anyMatch(type -> matches(stack, type));
    }

    public static boolean matches(ItemStack stack, String type) {
        return switch (type) {
            case "bow" -> stack.getItem() instanceof BowItem;
            case "crossbow" -> stack.getItem() instanceof CrossbowItem;
            case "pickaxe" -> stack.canPerformAction(ItemAbilities.PICKAXE_DIG);
            case "shovel" -> stack.canPerformAction(ItemAbilities.SHOVEL_DIG);
            case "heavy_weapon" -> canDisableShield(stack);
            case "sword" -> !canDisableShield(stack)
                && stack.canPerformAction(ItemAbilities.SWORD_DIG);
            case "shield" -> stack.canPerformAction(ItemAbilities.SHIELD_BLOCK);
            case "trident" -> stack.getItem() instanceof TridentItem;
            case "wand" -> JeicyLootCategories.isEidolonWand(stack);
            case "helmet", "chestplate", "leggings", "boots" ->
                LootCategory.forItem(stack).getKey().getPath().equals(type);
            default -> false;
        };
    }

    private static boolean canDisableShield(ItemStack stack) {
        try {
            return stack.canDisableShield(new ItemStack(Items.SHIELD), null, null);
        }
        catch (RuntimeException ex) {
            return false;
        }
    }
}
