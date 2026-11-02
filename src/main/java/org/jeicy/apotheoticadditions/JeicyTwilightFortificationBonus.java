package org.jeicy.apotheoticadditions;

import java.util.Collections;
import java.util.LinkedHashMap;
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
import dev.shadowsoffire.apothic_attributes.api.AbilityCooldowns;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import twilightforest.init.TFDataAttachments;
import twilightforest.init.TFSounds;

/** Faithful 1.21.1 port of Apotheosis 7.x's Twilight fortification bonus. */
public final class JeicyTwilightFortificationBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyTwilightTreasureGoblinBonus.TriggerData> DATA_CODEC =
        JeicyTwilightTreasureGoblinBonus.DATA_CODEC;

    public static final Codec<JeicyTwilightFortificationBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        Codec.unboundedMap(Codec.STRING, DATA_CODEC).fieldOf("values")
            .forGetter(b -> b.serializedValues),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(b -> b.originalSocketTypes)
    ).apply(inst, JeicyTwilightFortificationBonus::new));

    private final Map<OriginalGemRank, JeicyTwilightTreasureGoblinBonus.TriggerData> values;
    private final Map<String, JeicyTwilightTreasureGoblinBonus.TriggerData> serializedValues;
    private final List<String> originalSocketTypes;

    public JeicyTwilightFortificationBonus(GemClass gemClass,
            Map<String, JeicyTwilightTreasureGoblinBonus.TriggerData> rawValues,
            List<String> originalSocketTypes) {
        super(gemClass);
        if (rawValues.isEmpty()) throw new IllegalArgumentException("Fortification values cannot be empty");
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        Map<OriginalGemRank, JeicyTwilightTreasureGoblinBonus.TriggerData> byRank =
            new LinkedHashMap<>();
        Map<String, JeicyTwilightTreasureGoblinBonus.TriggerData> serialized =
            new LinkedHashMap<>();
        rawValues.forEach((key, value) -> {
            OriginalGemRank rank = OriginalGemRank.fromOriginalId(key);
            if (!Float.isFinite(value.chance()) || value.chance() < 0 || value.chance() > 1
                || value.cooldown() < 0) {
                throw new IllegalArgumentException("Invalid Twilight trigger data: " + value);
            }
            byRank.put(rank, value);
            serialized.put(rank.originalId(), value);
        });
        this.values = Collections.unmodifiableMap(byRank);
        this.serializedValues = Collections.unmodifiableMap(serialized);
        this.originalSocketTypes = List.copyOf(originalSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return OriginalSocketMatcher.matchesAny(socketed, originalSocketTypes);
    }

    private JeicyTwilightTreasureGoblinBonus.TriggerData data(GemView gem) {
        OriginalGemRank rank = JeicyGemRankAccess.get(gem.gemStack());
        return rank == null ? null : values.get(rank);
    }

    @Override
    public void doPostHurt(GemInstance gem, LivingEntity user, DamageSource source) {
        var value = data(gem);
        if (value == null || user.level().isClientSide()) return;
        ResourceLocation cooldownId = makeUniqueId(gem);
        if (AbilityCooldowns.isOnCooldown(user, cooldownId, value.cooldown())) return;
        if (user.getRandom().nextFloat() > value.chance()) return;

        // Twilight Forest 1.20's replenishShields() set five temporary shields.
        // The 1.21 attachment API exposes that operation as setShields(..., true).
        user.getData(TFDataAttachments.FORTIFICATION_SHIELDS).setShields(user, 5, true);
        user.level().playSound(null, user.blockPosition(), TFSounds.SHIELD_ADD.get(),
            SoundSource.PLAYERS, 1.0F,
            (user.getRandom().nextFloat() - user.getRandom().nextFloat()) * 0.2F + 1.0F);
        AbilityCooldowns.startCooldown(user, cooldownId);
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        var value = data(gem);
        if (value == null) return Component.empty();
        Component cooldown = Component.translatable("affix.apotheosis.cooldown",
            StringUtil.formatTickDuration(value.cooldown(), ctx.tickRate()));
        return Component.translatable("bonus.apotheosis:twilight_fortification.desc",
            Affix.fmt(value.chance() * 100), cooldown).withStyle(ChatFormatting.YELLOW);
    }

    JeicyTwilightTreasureGoblinBonus.TriggerData valueForRank(OriginalGemRank rank) {
        return values.get(rank);
    }

    static int replenishedTemporaryShieldsForVerification() {
        return 5;
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
