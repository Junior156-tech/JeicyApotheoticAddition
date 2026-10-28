package org.jeicy.apotheoticadditions;

import java.util.Map;
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
 * Runtime compilation against Apotheosis 8.8.0 and NeoForge is still pending.
 */
public final class JeicyTieredAttributeBonus extends GemBonus {
    public static final Codec<JeicyTieredAttributeBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(a -> a.attribute),
        PlaceboCodecs.enumCodec(Operation.class).fieldOf("operation").forGetter(a -> a.operation),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("values").forGetter(a -> a.values.serialized())
    ).apply(inst, JeicyTieredAttributeBonus::new));

    private final Holder<Attribute> attribute;
    private final Operation operation;
    private final OriginalRankValues values;

    public JeicyTieredAttributeBonus(GemClass gemClass, Holder<Attribute> attribute,
                                     Operation operation, Map<String, Double> rawValues) {
        super(gemClass);
        this.attribute = attribute;
        this.operation = operation;
        this.values = new OriginalRankValues(rawValues);
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
