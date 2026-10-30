package org.jeicy.apotheoticadditions;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Restores original equipment categories that Apotheosis 8 no longer ships. */
public final class JeicyLootCategories {
    private static final String[] CURIOS_ARMOR_SLOTS = {
        "body", "head", "back", "feet", "hands"
    };
    private static final String[] CURIOS_JEWELRY_SLOTS = {
        "ring", "an_focus", "necklace", "charm", "belt"
    };
    private static final TagKey<Item> AETHER_ACCESSORIES = itemTag("aether", "accessories");
    private static final DeferredRegister<LootCategory> CATEGORIES = DeferredRegister.create(
        Apoth.BuiltInRegs.LOOT_CATEGORY.key(), "apotheotic_additions");

    public static final DeferredHolder<LootCategory, LootCategory> WAND = CATEGORIES.register(
        "wand", () -> new LootCategory(JeicyLootCategories::isEidolonWand,
            ALObjects.EquipmentSlotGroups.MAINHAND, 500));
    public static final DeferredHolder<LootCategory, LootCategory> CURIOS_ARMOR = CATEGORIES.register(
        "curios_armor", () -> new LootCategory(JeicyLootCategories::isCuriosArmor,
            ALObjects.EquipmentSlotGroups.ANY, 510));
    public static final DeferredHolder<LootCategory, LootCategory> CURIOS_JEWELRY = CATEGORIES.register(
        "curios_jewelry", () -> new LootCategory(JeicyLootCategories::isCuriosJewelry,
            ALObjects.EquipmentSlotGroups.ANY, 510));
    public static final DeferredHolder<LootCategory, LootCategory> AETHER_ACCESSORY = CATEGORIES.register(
        "aether_accessory", () -> new LootCategory(JeicyLootCategories::isAetherAccessory,
            ALObjects.EquipmentSlotGroups.ANY, 520));

    private JeicyLootCategories() {}

    public static void register(IEventBus modBus) {
        CATEGORIES.register(modBus);
    }

    public static boolean isEidolonWand(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return "eidolon_repraised".equals(id.getNamespace())
            && ("bonechill_wand".equals(id.getPath()) || "soulfire_wand".equals(id.getPath()));
    }

    public static boolean isCuriosArmor(ItemStack stack) {
        return matchesCuriosSlots(stack, CURIOS_ARMOR_SLOTS);
    }

    public static boolean isCuriosJewelry(ItemStack stack) {
        return matchesCuriosSlots(stack, CURIOS_JEWELRY_SLOTS);
    }

    public static boolean isAetherAccessory(ItemStack stack) {
        return stack.is(AETHER_ACCESSORIES);
    }

    public static boolean isCuriosSlot(ItemStack stack, String slot) {
        return stack.is(itemTag("curios", slot));
    }

    private static boolean matchesCuriosSlots(ItemStack stack, String[] slots) {
        for (String slot : slots) {
            if (isCuriosSlot(stack, slot)) return true;
        }
        return false;
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
