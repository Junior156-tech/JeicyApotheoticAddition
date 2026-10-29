package org.jeicy.apotheoticadditions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.affix.effect.MobEffectAffix.Target;
import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.MobEffectBonus;
import dev.shadowsoffire.apothic_attributes.api.AbilityCooldowns;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/** Two original attack effects which shared melee_weapon after the 8.x category merge. */
public final class JeicySplitAttackEffectBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<Profile> PROFILE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mob_effect").forGetter(p -> p.effect),
        Codec.unboundedMap(Codec.STRING, JeicyTieredMobEffectBonus.EFFECT_DATA_CODEC)
            .fieldOf("values").forGetter(p -> p.serializedValues),
        Codec.BOOL.optionalFieldOf("stack_on_reapply", false).forGetter(p -> p.stackOnReapply),
        Codec.intRange(1, 255).optionalFieldOf("stacking_limit", 255).forGetter(p -> p.stackingLimit),
        Codec.STRING.listOf().fieldOf("original_socket_types").forGetter(p -> p.socketTypes)
    ).apply(inst, Profile::new));

    public static final Codec<JeicySplitAttackEffectBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        PROFILE_CODEC.fieldOf("heavy_profile").forGetter(b -> b.heavy),
        PROFILE_CODEC.fieldOf("light_profile").forGetter(b -> b.light)
    ).apply(inst, JeicySplitAttackEffectBonus::new));

    private final Profile heavy;
    private final Profile light;

    public JeicySplitAttackEffectBonus(GemClass gemClass, Profile heavy, Profile light) {
        super(gemClass);
        this.heavy = heavy;
        this.light = light;
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return heavy.matches(socketed) || light.matches(socketed);
    }

    @Override
    public void doPostAttack(GemInstance gem, LivingEntity user, Entity attacked) {
        if (!(attacked instanceof LivingEntity recipient)) return;
        ItemStack weapon = user.getMainHandItem();
        if (heavy.matches(weapon)) heavy.apply(gem, recipient, "heavy");
        else if (light.matches(weapon)) light.apply(gem, recipient, "light");
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        MutableComponent result = heavy.tooltip(gem, ctx);
        Component lightText = light.tooltip(gem, ctx);
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
        return heavy.values.get(rank);
    }

    JeicyTieredMobEffectBonus.EffectData lightValueForRank(OriginalGemRank rank) {
        return light.values.get(rank);
    }

    boolean lightStacksOnReapply() {
        return light.stackOnReapply;
    }

    int lightStackedAmplifierForVerification(int existingAmplifier, OriginalGemRank rank) {
        return light.stackedAmplifier(existingAmplifier, light.values.get(rank));
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    public static final class Profile {
        private final Holder<MobEffect> effect;
        private final Map<OriginalGemRank, JeicyTieredMobEffectBonus.EffectData> values;
        private final Map<String, JeicyTieredMobEffectBonus.EffectData> serializedValues;
        private final boolean stackOnReapply;
        private final int stackingLimit;
        private final List<String> socketTypes;

        public Profile(Holder<MobEffect> effect,
                       Map<String, JeicyTieredMobEffectBonus.EffectData> rawValues,
                       boolean stackOnReapply, int stackingLimit, List<String> socketTypes) {
            if (rawValues.isEmpty()) throw new IllegalArgumentException("Effect values cannot be empty");
            if (socketTypes.isEmpty()) throw new IllegalArgumentException("Socket types cannot be empty");
            Map<OriginalGemRank, JeicyTieredMobEffectBonus.EffectData> parsed = new LinkedHashMap<>();
            Map<String, JeicyTieredMobEffectBonus.EffectData> serialized = new LinkedHashMap<>();
            rawValues.forEach((key, value) -> {
                OriginalGemRank rank = OriginalGemRank.fromOriginalId(key);
                parsed.put(rank, value);
                serialized.put(rank.originalId(), value);
            });
            this.effect = effect;
            this.values = Collections.unmodifiableMap(parsed);
            this.serializedValues = Collections.unmodifiableMap(serialized);
            this.stackOnReapply = stackOnReapply;
            this.stackingLimit = stackingLimit;
            this.socketTypes = List.copyOf(socketTypes);
        }

        private boolean matches(ItemStack stack) {
            return OriginalSocketMatcher.matchesAny(stack, socketTypes);
        }

        private void apply(GemInstance gem, LivingEntity recipient, String profileId) {
            var value = values.get(JeicyGemRankAccess.get(gem.gemStack()));
            if (value == null || recipient.level().isClientSide()) return;
            var cooldownId = makeUniqueId(gem, profileId);
            if (value.cooldown() != 0
                && AbilityCooldowns.isOnCooldown(recipient, cooldownId, value.cooldown())) return;
            MobEffectInstance existing = recipient.getEffect(effect);
            if (stackOnReapply && existing != null) {
                int duration = Math.max(existing.getDuration(), value.duration());
                int amplifier = stackedAmplifier(existing.getAmplifier(), value);
                recipient.addEffect(new MobEffectInstance(effect, duration, amplifier,
                    existing.isAmbient(), existing.isVisible(), existing.showIcon()));
            }
            else recipient.addEffect(value.build(effect));
            if (value.cooldown() != 0) AbilityCooldowns.startCooldown(recipient, cooldownId);
        }

        private int stackedAmplifier(int existingAmplifier,
                                     JeicyTieredMobEffectBonus.EffectData value) {
            return Math.min(stackingLimit, existingAmplifier + 1 + value.amplifier());
        }

        private MutableComponent tooltip(GemView gem, AttributeTooltipContext ctx) {
            var value = values.get(JeicyGemRankAccess.get(gem.gemStack()));
            if (value == null) return Component.empty();
            MutableComponent result = Target.ATTACK_TARGET.toComponent(
                MobEffectBonus.toComponent(value.build(effect), ctx.tickRate()))
                .withStyle(ChatFormatting.YELLOW);
            if (value.cooldown() != 0) {
                result.append(" ").append(Component.translatable("affix.apotheosis.cooldown",
                    StringUtil.formatTickDuration(value.cooldown(), ctx.tickRate())));
            }
            if (stackOnReapply) result.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
            return result;
        }
    }
}
