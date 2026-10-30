package org.jeicy.apotheoticadditions;

import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

/** Original-rank equivalent of Apotheosis' enchantment gem bonus. */
public final class JeicyTieredEnchantmentBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyTieredEnchantmentBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        Enchantment.CODEC.fieldOf("enchantment").forGetter(b -> b.enchantment),
        Codec.BOOL.optionalFieldOf("global", false).forGetter(b -> b.global),
        Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("values")
            .forGetter(b -> b.values.serialized()),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(b -> b.originalSocketTypes)
    ).apply(inst, JeicyTieredEnchantmentBonus::new));

    private final Holder<Enchantment> enchantment;
    private final boolean global;
    private final OriginalIntegerRankValues values;
    private final List<String> originalSocketTypes;

    public JeicyTieredEnchantmentBonus(GemClass gemClass, Holder<Enchantment> enchantment,
                                       boolean global, Map<String, Integer> rawValues,
                                       List<String> originalSocketTypes) {
        super(gemClass);
        this.enchantment = enchantment;
        this.global = global;
        this.values = new OriginalIntegerRankValues(rawValues);
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        this.originalSocketTypes = List.copyOf(originalSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return OriginalSocketMatcher.matchesAny(socketed, originalSocketTypes);
    }

    private Integer originalValue(GemView gem) {
        OriginalGemRank rank = JeicyGemRankAccess.get(gem.gemStack());
        return rank == null ? null : values.get(rank);
    }

    @Override
    public boolean supports(Purity purity) {
        return true;
    }

    @Override
    public void getEnchantmentLevels(GemInstance gem, GetEnchantmentLevelEvent event) {
        if (!matchesSocket(event.getStack())) return;
        Integer value = originalValue(gem);
        if (value == null || value == 0) return;

        ItemEnchantments.Mutable enchantments = event.getEnchantments();
        if (global) {
            for (Holder<Enchantment> holder : List.copyOf(enchantments.keySet())) {
                int current = enchantments.getLevel(holder);
                if (current > 0) enchantments.upgrade(holder, current + value);
            }
        }
        else {
            enchantments.upgrade(enchantment, enchantments.getLevel(enchantment) + value);
        }
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        Integer value = originalValue(gem);
        if (value == null) return Component.empty();
        Component levels = Component.translatable(
            value > 1 ? "misc.apotheosis.level.many" : "misc.apotheosis.level");
        String key = global
            ? "bonus.apotheosis:enchantment.desc.global"
            : "bonus.apotheosis:enchantment.desc";
        return Component.translatable(key, value, levels, enchantment.value().description())
            .withStyle(ChatFormatting.GREEN);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    Integer valueForRank(OriginalGemRank rank) {
        return values.get(rank);
    }
}
