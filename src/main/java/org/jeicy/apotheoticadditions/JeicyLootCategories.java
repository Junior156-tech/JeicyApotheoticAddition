package org.jeicy.apotheoticadditions;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Restores original equipment categories that Apotheosis 8 no longer ships. */
public final class JeicyLootCategories {
    private static final DeferredRegister<LootCategory> CATEGORIES = DeferredRegister.create(
        Apoth.BuiltInRegs.LOOT_CATEGORY.key(), "apotheotic_additions");

    public static final DeferredHolder<LootCategory, LootCategory> WAND = CATEGORIES.register(
        "wand", () -> new LootCategory(JeicyLootCategories::isEidolonWand,
            ALObjects.EquipmentSlotGroups.MAINHAND, 500));

    private JeicyLootCategories() {}

    public static void register(IEventBus modBus) {
        CATEGORIES.register(modBus);
    }

    public static boolean isEidolonWand(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return "eidolon_repraised".equals(id.getNamespace())
            && ("bonechill_wand".equals(id.getPath()) || "soulfire_wand".equals(id.getPath()));
    }
}
