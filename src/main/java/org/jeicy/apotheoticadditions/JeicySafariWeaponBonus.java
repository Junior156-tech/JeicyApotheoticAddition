package org.jeicy.apotheoticadditions;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.affix.effect.MobEffectAffix.Target;
import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiersEvent;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/**
 * Preserves Safari's heavy-weapon Exsanguination and light-weapon critical
 * chance after Apotheosis 8 merged both original categories into melee_weapon.
 */
public final class JeicySafariWeaponBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicySafariWeaponBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("heavy_mob_effect")
            .forGetter(b -> b.heavyEffect),
        Target.CODEC.fieldOf("heavy_target").forGetter(b -> b.heavyTarget),
        Codec.unboundedMap(Codec.STRING, JeicyTieredMobEffectBonus.EFFECT_DATA_CODEC)
            .fieldOf("heavy_values").forGetter(b -> b.heavyValues),
        Codec.BOOL.optionalFieldOf("heavy_stack_on_reapply", false)
            .forGetter(b -> b.heavyStackOnReapply),
        Codec.intRange(1, 255).optionalFieldOf("heavy_stacking_limit", 255)
            .forGetter(b -> b.heavyStackingLimit),
        Codec.STRING.listOf().fieldOf("heavy_original_socket_types")
            .forGetter(b -> b.heavySocketTypes),
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("light_attribute")
            .forGetter(b -> b.lightAttribute),
        PlaceboCodecs.enumCodec(Operation.class).fieldOf("light_operation")
            .forGetter(b -> b.lightOperation),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("light_values")
            .forGetter(b -> b.lightValues),
        Codec.STRING.listOf().fieldOf("light_original_socket_types")
            .forGetter(b -> b.lightSocketTypes)
    ).apply(inst, JeicySafariWeaponBonus::new));

    private final Holder<MobEffect> heavyEffect;
    private final Target heavyTarget;
    private final Map<String, JeicyTieredMobEffectBonus.EffectData> heavyValues;
    private final boolean heavyStackOnReapply;
    private final int heavyStackingLimit;
    private final List<String> heavySocketTypes;
    private final Holder<Attribute> lightAttribute;
    private final Operation lightOperation;
    private final Map<String, Double> lightValues;
    private final List<String> lightSocketTypes;
    private final JeicyTieredMobEffectBonus heavy;
    private final JeicyTieredAttributeBonus light;

    public JeicySafariWeaponBonus(GemClass gemClass, Holder<MobEffect> heavyEffect,
                                  Target heavyTarget,
                                  Map<String, JeicyTieredMobEffectBonus.EffectData> heavyValues,
                                  boolean heavyStackOnReapply, int heavyStackingLimit,
                                  List<String> heavySocketTypes, Holder<Attribute> lightAttribute,
                                  Operation lightOperation, Map<String, Double> lightValues,
                                  List<String> lightSocketTypes) {
        super(gemClass);
        if (heavyTarget != Target.ATTACK_TARGET) {
            throw new IllegalArgumentException("Safari heavy profile must target the attacked entity");
        }
        this.heavyEffect = heavyEffect;
        this.heavyTarget = heavyTarget;
        this.heavyValues = Map.copyOf(heavyValues);
        this.heavyStackOnReapply = heavyStackOnReapply;
        this.heavyStackingLimit = heavyStackingLimit;
        this.heavySocketTypes = List.copyOf(heavySocketTypes);
        this.lightAttribute = lightAttribute;
        this.lightOperation = lightOperation;
        this.lightValues = Map.copyOf(lightValues);
        this.lightSocketTypes = List.copyOf(lightSocketTypes);
        this.heavy = new JeicyTieredMobEffectBonus(gemClass, heavyEffect, heavyTarget,
            heavyValues, heavyStackOnReapply, heavyStackingLimit, heavySocketTypes);
        this.light = new JeicyTieredAttributeBonus(gemClass, lightAttribute, lightOperation,
            lightValues, lightSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return heavy.matchesSocket(socketed) || light.matchesSocket(socketed);
    }

    @Override
    public void addModifiers(GemInstance gem, StackAttributeModifiersEvent event) {
        light.addModifiers(gem, event);
    }

    @Override
    public void skipModifierIds(GemInstance gem, Consumer<ResourceLocation> skip) {
        light.skipModifierIds(gem, skip);
    }

    @Override
    public void doPostAttack(GemInstance gem, LivingEntity user, Entity attacked) {
        if (heavy.matchesSocket(user.getMainHandItem())) heavy.doPostAttack(gem, user, attacked);
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        MutableComponent result = heavy.getSocketBonusTooltip(gem, ctx).copy();
        Component lightText = light.getSocketBonusTooltip(gem, ctx);
        if (!result.getString().isEmpty() && !lightText.getString().isEmpty()) {
            result.append(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY));
        }
        return result.append(lightText);
    }

    @Override
    public boolean supports(Purity purity) {
        return true;
    }

    JeicyTieredMobEffectBonus.EffectData heavyValueForRank(OriginalGemRank rank) {
        return heavy.valueForRank(rank);
    }

    boolean heavyStacksOnReapply() {
        return heavy.stacksOnReapply();
    }

    int heavyStackedAmplifierForVerification(int existingAmplifier, OriginalGemRank rank) {
        return heavy.stackedAmplifierForVerification(existingAmplifier, rank);
    }

    Double lightValueForRank(OriginalGemRank rank) {
        return light.valueForRank(rank);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
