package org.jeicy.apotheoticadditions;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiersEvent;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/** Original all-stats gem bonus backed by the independent nine-rank axis. */
public final class JeicyTieredAllStatsBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyTieredAllStatsBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        PlaceboCodecs.enumCodec(Operation.class).fieldOf("operation")
            .forGetter(b -> b.operation),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("values")
            .forGetter(b -> b.values.serialized()),
        RegistryCodecs.homogeneousList(Registries.ATTRIBUTE).fieldOf("attributes")
            .forGetter(b -> b.attributes),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(b -> b.originalSocketTypes)
    ).apply(inst, JeicyTieredAllStatsBonus::new));

    private final Operation operation;
    private final OriginalRankValues values;
    private final HolderSet<Attribute> attributes;
    private final List<String> originalSocketTypes;

    public JeicyTieredAllStatsBonus(GemClass gemClass, Operation operation,
                                    Map<String, Double> rawValues,
                                    HolderSet<Attribute> attributes,
                                    List<String> originalSocketTypes) {
        super(gemClass);
        if (attributes.size() == 0) throw new IllegalArgumentException("Attributes cannot be empty");
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        this.operation = operation;
        this.values = new OriginalRankValues(rawValues);
        this.attributes = attributes;
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
    public boolean supports(Purity purity) {
        return true;
    }

    @Override
    public void addModifiers(GemInstance gem, StackAttributeModifiersEvent event) {
        if (!matchesSocket(event.getItemStack())) return;
        Double value = originalValue(gem);
        if (value == null) return;

        int index = 0;
        for (Holder<Attribute> attribute : attributes) {
            ResourceLocation id = makeUniqueId(gem, "all_stats_" + index++);
            event.addModifier(attribute, new AttributeModifier(id, value, operation),
                gem.category().getSlots());
        }
    }

    @Override
    public void skipModifierIds(GemInstance gem, Consumer<ResourceLocation> skip) {
        if (originalValue(gem) == null) return;
        for (int index = 0; index < attributes.size(); index++) {
            skip.accept(makeUniqueId(gem, "all_stats_" + index));
        }
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        Double value = originalValue(gem);
        if (value == null) return Component.empty();
        return Component.translatable("bonus.apotheosis:all_stats.desc",
            Affix.fmt(value.floatValue() * 100)).withStyle(ChatFormatting.YELLOW);
    }

    int attributeCount() {
        return attributes.size();
    }

    Double valueForRank(OriginalGemRank rank) {
        return values.get(rank);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
