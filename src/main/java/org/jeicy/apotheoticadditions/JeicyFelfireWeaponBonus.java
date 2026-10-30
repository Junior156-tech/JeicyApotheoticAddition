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
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/**
 * Preserves Felfire's separate heavy-weapon attributes and light-weapon effect
 * after Apotheosis 8 merged both original categories into melee_weapon.
 */
public final class JeicyFelfireWeaponBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyFelfireWeaponBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        JeicyTieredMultiAttributeBonus.PROFILE_CODEC.listOf().fieldOf("heavy_profiles")
            .forGetter(b -> b.heavyProfiles),
        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("light_mob_effect")
            .forGetter(b -> b.lightEffect),
        Target.CODEC.fieldOf("light_target").forGetter(b -> b.lightTarget),
        Codec.unboundedMap(Codec.STRING, JeicyTieredMobEffectBonus.EFFECT_DATA_CODEC)
            .fieldOf("light_values").forGetter(b -> b.lightValues),
        Codec.BOOL.optionalFieldOf("light_stack_on_reapply", false)
            .forGetter(b -> b.lightStackOnReapply),
        Codec.intRange(1, 255).optionalFieldOf("light_stacking_limit", 255)
            .forGetter(b -> b.lightStackingLimit),
        Codec.STRING.listOf().fieldOf("light_original_socket_types")
            .forGetter(b -> b.lightSocketTypes)
    ).apply(inst, JeicyFelfireWeaponBonus::new));

    private final List<JeicyTieredMultiAttributeBonus.Profile> heavyProfiles;
    private final Holder<MobEffect> lightEffect;
    private final Target lightTarget;
    private final Map<String, JeicyTieredMobEffectBonus.EffectData> lightValues;
    private final boolean lightStackOnReapply;
    private final int lightStackingLimit;
    private final List<String> lightSocketTypes;
    private final JeicyTieredMultiAttributeBonus heavy;
    private final JeicyTieredMobEffectBonus light;

    public JeicyFelfireWeaponBonus(GemClass gemClass,
                                   List<JeicyTieredMultiAttributeBonus.Profile> heavyProfiles,
                                   Holder<MobEffect> lightEffect, Target lightTarget,
                                   Map<String, JeicyTieredMobEffectBonus.EffectData> lightValues,
                                   boolean lightStackOnReapply, int lightStackingLimit,
                                   List<String> lightSocketTypes) {
        super(gemClass);
        if (lightTarget != Target.ATTACK_TARGET) {
            throw new IllegalArgumentException("Felfire light profile must target the attacked entity");
        }
        this.heavyProfiles = List.copyOf(heavyProfiles);
        this.lightEffect = lightEffect;
        this.lightTarget = lightTarget;
        this.lightValues = Map.copyOf(lightValues);
        this.lightStackOnReapply = lightStackOnReapply;
        this.lightStackingLimit = lightStackingLimit;
        this.lightSocketTypes = List.copyOf(lightSocketTypes);
        this.heavy = new JeicyTieredMultiAttributeBonus(gemClass, heavyProfiles);
        this.light = new JeicyTieredMobEffectBonus(gemClass, lightEffect, lightTarget,
            lightValues, lightStackOnReapply, lightStackingLimit, lightSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return heavy.matchesSocket(socketed) || light.matchesSocket(socketed);
    }

    @Override
    public void addModifiers(GemInstance gem, StackAttributeModifiersEvent event) {
        heavy.addModifiers(gem, event);
    }

    @Override
    public void skipModifierIds(GemInstance gem, Consumer<ResourceLocation> skip) {
        heavy.skipModifierIds(gem, skip);
    }

    @Override
    public void doPostAttack(GemInstance gem, LivingEntity user, Entity attacked) {
        if (light.matchesSocket(user.getMainHandItem())) light.doPostAttack(gem, user, attacked);
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

    Double heavyValueForRank(int modifierIndex, OriginalGemRank rank) {
        return heavy.valueForRank(0, modifierIndex, rank);
    }

    JeicyTieredMobEffectBonus.EffectData lightValueForRank(OriginalGemRank rank) {
        return light.valueForRank(rank);
    }

    boolean lightStacksOnReapply() {
        return light.stacksOnReapply();
    }

    int lightStackedAmplifierForVerification(int existingAmplifier, OriginalGemRank rank) {
        return light.stackedAmplifierForVerification(existingAmplifier, rank);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
