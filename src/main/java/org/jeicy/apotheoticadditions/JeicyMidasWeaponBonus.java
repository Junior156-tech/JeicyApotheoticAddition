package org.jeicy.apotheoticadditions;

import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

/**
 * Preserves Midas' distinct sword/trident and heavy-weapon effects after both
 * old categories were merged into melee_weapon by Apotheosis 8.x.
 */
public final class JeicyMidasWeaponBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyMidasWeaponBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        Enchantment.CODEC.fieldOf("enchantment").forGetter(b -> b.enchantment),
        Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("enchantment_values")
            .forGetter(b -> b.enchantmentValues.serialized()),
        ContextAwarePredicate.CODEC.fieldOf("conditions").forGetter(b -> b.conditions),
        Ingredient.CODEC_NONEMPTY.fieldOf("inputs").forGetter(b -> b.inputs),
        ItemStack.CODEC.fieldOf("output").forGetter(b -> b.output),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("transform_values")
            .forGetter(b -> b.transformValues.serialized()),
        Codec.STRING.fieldOf("desc").forGetter(b -> b.descKey),
        Codec.STRING.listOf().fieldOf("light_socket_types").forGetter(b -> b.lightSocketTypes),
        Codec.STRING.listOf().fieldOf("heavy_socket_types").forGetter(b -> b.heavySocketTypes)
    ).apply(inst, JeicyMidasWeaponBonus::new));

    private final Holder<Enchantment> enchantment;
    private final OriginalIntegerRankValues enchantmentValues;
    private final ContextAwarePredicate conditions;
    private final Ingredient inputs;
    private final ItemStack output;
    private final OriginalRankValues transformValues;
    private final String descKey;
    private final List<String> lightSocketTypes;
    private final List<String> heavySocketTypes;

    public JeicyMidasWeaponBonus(GemClass gemClass, Holder<Enchantment> enchantment,
                                 Map<String, Integer> rawEnchantmentValues,
                                 ContextAwarePredicate conditions, Ingredient inputs,
                                 ItemStack output, Map<String, Double> rawTransformValues,
                                 String descKey, List<String> lightSocketTypes,
                                 List<String> heavySocketTypes) {
        super(gemClass);
        if (output.isEmpty()) throw new IllegalArgumentException("Midas output cannot be empty");
        if (lightSocketTypes.isEmpty() || heavySocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Midas socket profiles cannot be empty");
        }
        if (rawTransformValues.values().stream().anyMatch(value -> value < 0 || value > 1)) {
            throw new IllegalArgumentException("Drop transform chances must be between zero and one");
        }
        this.enchantment = enchantment;
        this.enchantmentValues = new OriginalIntegerRankValues(rawEnchantmentValues);
        this.conditions = conditions;
        this.inputs = inputs;
        this.output = output.copy();
        this.transformValues = new OriginalRankValues(rawTransformValues);
        this.descKey = descKey;
        this.lightSocketTypes = List.copyOf(lightSocketTypes);
        this.heavySocketTypes = List.copyOf(heavySocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return isLightWeapon(socketed) || isHeavyWeapon(socketed);
    }

    private boolean isLightWeapon(ItemStack stack) {
        return OriginalSocketMatcher.matchesAny(stack, lightSocketTypes);
    }

    private boolean isHeavyWeapon(ItemStack stack) {
        return OriginalSocketMatcher.matchesAny(stack, heavySocketTypes);
    }

    private OriginalGemRank originalRank(GemView gem) {
        return JeicyGemRankAccess.get(gem.gemStack());
    }

    @Override
    public void getEnchantmentLevels(GemInstance gem, GetEnchantmentLevelEvent event) {
        if (!isLightWeapon(event.getStack())) return;
        Integer value = enchantmentValues.get(originalRank(gem));
        if (value == null || value == 0) return;
        ItemEnchantments.Mutable enchantments = event.getEnchantments();
        enchantments.upgrade(enchantment, enchantments.getLevel(enchantment) + value);
    }

    @Override
    public void modifyLoot(GemInstance gem, ObjectArrayList<ItemStack> loot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool == null || !isHeavyWeapon(tool) || !conditions.matches(context)) return;
        Double chance = transformValues.get(originalRank(gem));
        if (chance == null || context.getRandom().nextFloat() > chance) return;

        for (int index = 0; index < loot.size(); index++) {
            ItemStack stack = loot.get(index);
            if (inputs.test(stack)) {
                ItemStack replacement = output.copy();
                replacement.setCount(stack.getCount());
                loot.set(index, replacement);
            }
        }
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        OriginalGemRank rank = originalRank(gem);
        Integer enchantmentValue = enchantmentValues.get(rank);
        Double chance = transformValues.get(rank);
        if (enchantmentValue == null && chance == null) return Component.empty();

        MutableComponent result = Component.empty();
        if (enchantmentValue != null) {
            Component levels = Component.translatable(
                enchantmentValue > 1 ? "misc.apotheosis.level.many" : "misc.apotheosis.level");
            result.append(Component.translatable("bonus.apotheosis:enchantment.desc",
                enchantmentValue, levels, enchantment.value().description())
                .withStyle(ChatFormatting.GREEN));
        }
        if (enchantmentValue != null && chance != null) {
            result.append(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY));
        }
        if (chance != null) {
            result.append(Component.translatable(descKey, Affix.fmt(chance.floatValue() * 100))
                .withStyle(ChatFormatting.YELLOW));
        }
        return result;
    }

    Integer enchantmentValueForRank(OriginalGemRank rank) {
        return enchantmentValues.get(rank);
    }

    Double transformChanceForRank(OriginalGemRank rank) {
        return transformValues.get(rank);
    }

    ItemStack outputForVerification() {
        return output.copy();
    }

    @Override
    public boolean supports(Purity purity) {
        return true;
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
