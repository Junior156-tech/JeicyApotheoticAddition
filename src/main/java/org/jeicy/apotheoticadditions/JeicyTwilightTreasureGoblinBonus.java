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
import dev.shadowsoffire.placebo.color.GradientColor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import twilightforest.entity.monster.Redcap;

/** Faithful 1.21.1 port of Apotheosis 7.x's Twilight treasure-goblin bonus. */
public final class JeicyTwilightTreasureGoblinBonus extends GemBonus implements JeicyExactSocketBonus {
    private static final ResourceLocation REDCAP_ID = ResourceLocation.fromNamespaceAndPath(
        "twilightforest", "redcap");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(
        "apotheosis", "very_fast");
    private static final ResourceLocation HEALTH_ID = ResourceLocation.fromNamespaceAndPath(
        "apotheosis", "healmth");
    private static final String GOBLIN_MARKER = "apoth.treasure_goblin";

    public static final Codec<TriggerData> DATA_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.FLOAT.fieldOf("chance").forGetter(TriggerData::chance),
        Codec.INT.fieldOf("cooldown").forGetter(TriggerData::cooldown)
    ).apply(inst, TriggerData::new));

    public static final Codec<JeicyTwilightTreasureGoblinBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        Codec.unboundedMap(Codec.STRING, DATA_CODEC).fieldOf("values")
            .forGetter(b -> b.serializedValues),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(b -> b.originalSocketTypes)
    ).apply(inst, JeicyTwilightTreasureGoblinBonus::new));

    private final Map<OriginalGemRank, TriggerData> values;
    private final Map<String, TriggerData> serializedValues;
    private final List<String> originalSocketTypes;

    public JeicyTwilightTreasureGoblinBonus(GemClass gemClass, Map<String, TriggerData> rawValues,
                                             List<String> originalSocketTypes) {
        super(gemClass);
        if (rawValues.isEmpty()) throw new IllegalArgumentException("Treasure goblin values cannot be empty");
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        Map<OriginalGemRank, TriggerData> byRank = new LinkedHashMap<>();
        Map<String, TriggerData> serialized = new LinkedHashMap<>();
        rawValues.forEach((key, value) -> {
            OriginalGemRank rank = OriginalGemRank.fromOriginalId(key);
            validate(value);
            byRank.put(rank, value);
            serialized.put(rank.originalId(), value);
        });
        this.values = Collections.unmodifiableMap(byRank);
        this.serializedValues = Collections.unmodifiableMap(serialized);
        this.originalSocketTypes = List.copyOf(originalSocketTypes);
    }

    private static void validate(TriggerData value) {
        if (!Float.isFinite(value.chance()) || value.chance() < 0 || value.chance() > 1
            || value.cooldown() < 0) {
            throw new IllegalArgumentException("Invalid Twilight trigger data: " + value);
        }
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return OriginalSocketMatcher.matchesAny(socketed, originalSocketTypes);
    }

    private TriggerData data(GemView gem) {
        OriginalGemRank rank = JeicyGemRankAccess.get(gem.gemStack());
        return rank == null ? null : values.get(rank);
    }

    @Override
    public void doPostAttack(GemInstance gem, net.minecraft.world.entity.LivingEntity user,
                             Entity attacked) {
        TriggerData value = data(gem);
        if (value == null || user.level().isClientSide()) return;
        ResourceLocation cooldownId = makeUniqueId(gem);
        if (AbilityCooldowns.isOnCooldown(user, cooldownId, value.cooldown())) return;
        if (user.getRandom().nextFloat() > value.chance()) return;

        Entity created = BuiltInRegistries.ENTITY_TYPE.getOptional(REDCAP_ID)
            .map(type -> type.create(user.level())).orElse(null);
        if (!(created instanceof Redcap goblin)) return;

        CompoundTag loot = new CompoundTag();
        loot.putString("DeathLootTable", "apotheosis:entity/treasure_goblin");
        goblin.readAdditionalSaveData(loot);
        goblin.getPersistentData().putBoolean(GOBLIN_MARKER, true);
        goblin.setCustomName(Component.translatable("name.apotheosis.treasure_goblin")
            .withStyle(style -> style.withColor(GradientColor.RAINBOW)));
        goblin.setCustomNameVisible(true);
        var speed = goblin.getAttribute(Attributes.MOVEMENT_SPEED);
        var health = goblin.getAttribute(Attributes.MAX_HEALTH);
        if (speed != null) {
            speed.addPermanentModifier(new AttributeModifier(SPEED_ID, 0.2,
                AttributeModifier.Operation.ADD_VALUE));
        }
        if (health != null) {
            health.addPermanentModifier(new AttributeModifier(HEALTH_ID, 60,
                AttributeModifier.Operation.ADD_VALUE));
        }
        goblin.setHealth(goblin.getMaxHealth());

        for (int attempt = 0; attempt < 8; attempt++) {
            int x = Mth.nextInt(goblin.getRandom(), -5, 5);
            int y = Mth.nextInt(goblin.getRandom(), -1, 1);
            int z = Mth.nextInt(goblin.getRandom(), -5, 5);
            goblin.setPos(attacked.position().add(x, y, z));
            if (user.level().noCollision(goblin)) break;
            if (attempt == 7) goblin.setPos(attacked.position());
        }
        goblin.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
        user.level().addFreshEntity(goblin);
        AbilityCooldowns.startCooldown(user, cooldownId);
    }

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Redcap goblin
            && goblin.getPersistentData().getBoolean(GOBLIN_MARKER)) {
            goblin.targetSelector.removeAllGoals(goal -> true);
            goblin.goalSelector.removeAllGoals(goal -> true);
            goblin.goalSelector.addGoal(10,
                new AvoidEntityGoal<>(goblin, Player.class, 6, 1, 1.25));
        }
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        TriggerData value = data(gem);
        if (value == null) return Component.empty();
        Component cooldown = Component.translatable("affix.apotheosis.cooldown",
            StringUtil.formatTickDuration(value.cooldown(), ctx.tickRate()));
        return Component.translatable("bonus.apotheosis:twilight_treasure_goblin.desc",
            Affix.fmt(value.chance() * 100), cooldown).withStyle(ChatFormatting.YELLOW);
    }

    TriggerData valueForRank(OriginalGemRank rank) {
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

    public record TriggerData(float chance, int cooldown) {}
}
