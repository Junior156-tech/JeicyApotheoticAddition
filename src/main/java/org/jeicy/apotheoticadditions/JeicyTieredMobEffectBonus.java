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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/** Mob-effect gem bonus driven by the original nine-rank value maps. */
public final class JeicyTieredMobEffectBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<EffectData> EFFECT_DATA_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.INT.fieldOf("duration").forGetter(EffectData::duration),
        Codec.INT.fieldOf("amplifier").forGetter(EffectData::amplifier),
        Codec.INT.optionalFieldOf("cooldown", 0).forGetter(EffectData::cooldown)
    ).apply(inst, EffectData::new));

    public static final Codec<JeicyTieredMobEffectBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mob_effect")
            .forGetter(b -> b.effect),
        Target.CODEC.fieldOf("target").forGetter(b -> b.target),
        Codec.unboundedMap(Codec.STRING, EFFECT_DATA_CODEC).fieldOf("values")
            .forGetter(b -> b.serializedValues),
        Codec.BOOL.optionalFieldOf("stack_on_reapply", false).forGetter(b -> b.stackOnReapply),
        Codec.intRange(1, 255).optionalFieldOf("stacking_limit", 255).forGetter(b -> b.stackingLimit),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(b -> b.originalSocketTypes)
    ).apply(inst, JeicyTieredMobEffectBonus::new));

    private final Holder<MobEffect> effect;
    private final Target target;
    private final Map<OriginalGemRank, EffectData> values;
    private final Map<String, EffectData> serializedValues;
    private final boolean stackOnReapply;
    private final int stackingLimit;
    private final List<String> originalSocketTypes;

    public JeicyTieredMobEffectBonus(GemClass gemClass, Holder<MobEffect> effect, Target target,
                                     Map<String, EffectData> rawValues,
                                     boolean stackOnReapply, int stackingLimit,
                                     List<String> originalSocketTypes) {
        super(gemClass);
        if (rawValues.isEmpty()) throw new IllegalArgumentException("Effect values cannot be empty");
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        Map<OriginalGemRank, EffectData> byRank = new LinkedHashMap<>();
        Map<String, EffectData> serialized = new LinkedHashMap<>();
        rawValues.forEach((key, value) -> {
            OriginalGemRank rank = OriginalGemRank.fromOriginalId(key);
            if (value.duration < 0 || value.amplifier < 0 || value.cooldown < 0) {
                throw new IllegalArgumentException("Mob effect values cannot be negative");
            }
            byRank.put(rank, value);
            serialized.put(rank.originalId(), value);
        });
        this.effect = effect;
        this.target = target;
        this.values = Collections.unmodifiableMap(byRank);
        this.serializedValues = Collections.unmodifiableMap(serialized);
        this.stackOnReapply = stackOnReapply;
        this.stackingLimit = stackingLimit;
        this.originalSocketTypes = List.copyOf(originalSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return OriginalSocketMatcher.matchesAny(socketed, originalSocketTypes);
    }

    private EffectData data(GemView gem) {
        OriginalGemRank rank = JeicyGemRankAccess.get(gem.gemStack());
        return rank == null ? null : values.get(rank);
    }

    @Override
    public boolean supports(Purity purity) {
        return true;
    }

    @Override
    public void doPostHurt(GemInstance gem, LivingEntity user, DamageSource source) {
        if (target == Target.HURT_SELF) applyEffect(gem, user);
        else if (target == Target.HURT_ATTACKER && source.getEntity() instanceof LivingEntity attacker) {
            applyEffect(gem, attacker);
        }
    }

    @Override
    public void doPostAttack(GemInstance gem, LivingEntity user, Entity attacked) {
        if (target == Target.ATTACK_SELF) applyEffect(gem, user);
        else if (target == Target.ATTACK_TARGET && attacked instanceof LivingEntity living) {
            applyEffect(gem, living);
        }
    }

    @Override
    public void onBlockBreak(GemInstance gem, Player player, LevelAccessor level,
                             BlockPos pos, BlockState state) {
        if (target == Target.BREAK_SELF) applyEffect(gem, player);
    }

    @Override
    public void onProjectileImpact(GemInstance gem, Projectile projectile, HitResult result) {
        if (result instanceof EntityHitResult entityHit
            && entityHit.getEntity() instanceof LivingEntity hit) {
            switch (target) {
                case ARROW_SELF -> {
                    if (projectile instanceof AbstractArrow
                        && projectile.getOwner() instanceof LivingEntity owner) applyEffect(gem, owner);
                }
                case ARROW_TARGET -> {
                    if (projectile instanceof AbstractArrow) applyEffect(gem, hit);
                }
                case PROJECTILE_SELF -> {
                    if (projectile.getOwner() instanceof LivingEntity owner) applyEffect(gem, owner);
                }
                case PROJECTILE_TARGET -> applyEffect(gem, hit);
                default -> { }
            }
        }
    }

    @Override
    public float onShieldBlock(GemInstance gem, LivingEntity user, DamageSource source,
                               float amount) {
        if (target == Target.BLOCK_SELF) applyEffect(gem, user);
        else if (target == Target.BLOCK_ATTACKER
            && source.getDirectEntity() instanceof LivingEntity attacker) applyEffect(gem, attacker);
        return amount;
    }

    private void applyEffect(GemInstance gem, LivingEntity recipient) {
        EffectData value = data(gem);
        if (value == null || recipient.level().isClientSide()) return;
        var cooldownId = makeUniqueId(gem);
        if (value.cooldown != 0
            && AbilityCooldowns.isOnCooldown(recipient, cooldownId, value.cooldown)) return;
        MobEffectInstance existing = recipient.getEffect(effect);
        if (stackOnReapply && existing != null) {
            int duration = Math.max(existing.getDuration(), value.duration());
            int amplifier = Math.min(stackingLimit,
                existing.getAmplifier() + 1 + value.amplifier());
            recipient.addEffect(new MobEffectInstance(effect, duration, amplifier,
                existing.isAmbient(), existing.isVisible(), existing.showIcon()));
        }
        else recipient.addEffect(value.build(effect));
        if (value.cooldown != 0) AbilityCooldowns.startCooldown(recipient, cooldownId);
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        EffectData value = data(gem);
        if (value == null) return Component.empty();
        MutableComponent result = target.toComponent(
            MobEffectBonus.toComponent(value.build(effect), ctx.tickRate()))
            .withStyle(ChatFormatting.YELLOW);
        if (value.cooldown != 0) {
            result.append(" ").append(Component.translatable("affix.apotheosis.cooldown",
                StringUtil.formatTickDuration(value.cooldown, ctx.tickRate())));
        }
        if (stackOnReapply) {
            result.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return result;
    }

    EffectData valueForRank(OriginalGemRank rank) {
        return values.get(rank);
    }

    boolean stacksOnReapply() {
        return stackOnReapply;
    }

    int stackedAmplifierForVerification(int existingAmplifier, OriginalGemRank rank) {
        EffectData value = values.get(rank);
        return Math.min(stackingLimit, existingAmplifier + 1 + value.amplifier());
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    public record EffectData(int duration, int amplifier, int cooldown) {
        MobEffectInstance build(Holder<MobEffect> effect) {
            return new MobEffectInstance(effect, duration, amplifier);
        }
    }
}
