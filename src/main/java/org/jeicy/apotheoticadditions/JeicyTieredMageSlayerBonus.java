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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/** Nine-rank form of the 1.20 Mageslayer helmet bonus. */
public final class JeicyTieredMageSlayerBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyTieredMageSlayerBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("values")
            .forGetter(b -> b.values.serialized()),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(b -> b.originalSocketTypes)
    ).apply(inst, JeicyTieredMageSlayerBonus::new));

    private final OriginalRankValues values;
    private final List<String> originalSocketTypes;

    public JeicyTieredMageSlayerBonus(GemClass gemClass, Map<String, Double> rawValues,
                                      List<String> originalSocketTypes) {
        super(gemClass);
        this.values = new OriginalRankValues(rawValues);
        if (rawValues.values().stream().anyMatch(value -> value < 0 || value > 1)) {
            throw new IllegalArgumentException("Mageslayer values must be between zero and one");
        }
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        this.originalSocketTypes = List.copyOf(originalSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return OriginalSocketMatcher.matchesAny(socketed, originalSocketTypes);
    }

    private Double originalValue(GemView gem) {
        OriginalGemRank rank = JeicyGemRankAccess.get(gem.gemStack());
        return rank == null ? null : values.get(rank);
    }

    @Override
    public float onHurt(GemInstance gem, DamageSource source, LivingEntity user, float amount) {
        Double value = originalValue(gem);
        if (value != null && source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            user.heal(amount * value.floatValue());
            return amount * (1 - value.floatValue());
        }
        return amount;
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        Double value = originalValue(gem);
        if (value == null) return Component.empty();
        return Component.translatable("bonus.apotheosis:mageslayer.desc",
            Affix.fmt(value.floatValue() * 100)).withStyle(ChatFormatting.YELLOW);
    }

    Double valueForRank(OriginalGemRank rank) {
        return values.get(rank);
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
