package org.jeicy.apotheoticadditions;

import java.util.Map;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiersEvent;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/**
 * Attribute gem bonus backed by Cisco's ORIGINAL nine rarity values.
 * This does not conflate Apotheosis purity with the original rarity axis.
 * It reads the real rank from the gem's stored ItemStack component, including
 * when the gem is inserted in an item (GemInstance implements GemView).
 *
 * Compiled and runtime-verified against Apotheosis 8.8.0 and NeoForge 21.1.251.
 */
public final class JeicyTieredAttributeBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyTieredAttributeBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(a -> a.attribute),
        PlaceboCodecs.enumCodec(Operation.class).fieldOf("operation").forGetter(a -> a.operation),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("values").forGetter(a -> a.values.serialized()),
        Codec.STRING.listOf().fieldOf("original_socket_types").forGetter(a -> a.originalSocketTypes)
    ).apply(inst, JeicyTieredAttributeBonus::new));

    private final Holder<Attribute> attribute;
    private final Operation operation;
    private final OriginalRankValues values;
    private final List<String> originalSocketTypes;

    public JeicyTieredAttributeBonus(GemClass gemClass, Holder<Attribute> attribute,
                                     Operation operation, Map<String, Double> rawValues,
                                     List<String> originalSocketTypes) {
        super(gemClass);
        this.attribute = attribute;
        this.operation = operation;
        this.values = new OriginalRankValues(rawValues);
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        this.originalSocketTypes = List.copyOf(originalSocketTypes);
    }

    @Override
    public boolean matchesSocket(net.minecraft.world.item.ItemStack socketed) {
        return OriginalSocketMatcher.matchesAny(socketed, this.originalSocketTypes);
    }

    private Double originalValue(GemView view) {
        OriginalGemRank rank = JeicyGemRankAccess.get(view.gemStack());
        return rank == null ? null : this.values.get(rank);
    }

    @Override
    public boolean supports(Purity purity) {
        // Apotheosis validates this axis before invoking the bonus. Actual
        // eligibility is determined by original_gem_rank, not by purity.
        // No rank component -> no attribute modifier is added.
        return true;
    }

    @Override
    public void addModifiers(GemInstance gem, StackAttributeModifiersEvent event) {
        if (!matchesSocket(event.getItemStack())) return;
        Double value = originalValue(gem);
        if (value != null) {
            AttributeModifier modifier = new AttributeModifier(makeUniqueId(gem), value, operation);
            event.addModifier(attribute, modifier, gem.category().getSlots());
        }
    }

    @Override
    public void skipModifierIds(GemInstance gem, Consumer<ResourceLocation> skip) {
        if (originalValue(gem) != null) skip.accept(makeUniqueId(gem));
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        Double value = originalValue(gem);
        if (value == null) return Component.empty();
        return attribute.value().toComponent(new AttributeModifier(makeUniqueId(gem), value, operation), ctx.flag());
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
