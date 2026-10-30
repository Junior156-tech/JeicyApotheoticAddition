package org.jeicy.apotheoticadditions;

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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/**
 * Preserves Candied's distinct heavy-weapon drop transform and light-weapon
 * Sugar Rush effect after Apotheosis 8 merged both categories into melee_weapon.
 */
public final class JeicyCandiedWeaponBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyCandiedWeaponBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        ContextAwarePredicate.CODEC.fieldOf("heavy_conditions").forGetter(b -> b.heavyConditions),
        Ingredient.CODEC_NONEMPTY.fieldOf("heavy_inputs").forGetter(b -> b.heavyInputs),
        ItemStack.CODEC.fieldOf("heavy_output").forGetter(b -> b.heavyOutput),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("heavy_values")
            .forGetter(b -> b.heavyValues),
        Codec.STRING.fieldOf("heavy_desc").forGetter(b -> b.heavyDesc),
        Codec.STRING.listOf().fieldOf("heavy_original_socket_types")
            .forGetter(b -> b.heavySocketTypes),
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
    ).apply(inst, JeicyCandiedWeaponBonus::new));

    private final ContextAwarePredicate heavyConditions;
    private final Ingredient heavyInputs;
    private final ItemStack heavyOutput;
    private final Map<String, Double> heavyValues;
    private final String heavyDesc;
    private final List<String> heavySocketTypes;
    private final Holder<MobEffect> lightEffect;
    private final Target lightTarget;
    private final Map<String, JeicyTieredMobEffectBonus.EffectData> lightValues;
    private final boolean lightStackOnReapply;
    private final int lightStackingLimit;
    private final List<String> lightSocketTypes;
    private final JeicyTieredDropTransformBonus heavy;
    private final JeicyTieredMobEffectBonus light;

    public JeicyCandiedWeaponBonus(GemClass gemClass, ContextAwarePredicate heavyConditions,
                                   Ingredient heavyInputs, ItemStack heavyOutput,
                                   Map<String, Double> heavyValues, String heavyDesc,
                                   List<String> heavySocketTypes, Holder<MobEffect> lightEffect,
                                   Target lightTarget,
                                   Map<String, JeicyTieredMobEffectBonus.EffectData> lightValues,
                                   boolean lightStackOnReapply, int lightStackingLimit,
                                   List<String> lightSocketTypes) {
        super(gemClass);
        if (lightTarget != Target.ATTACK_SELF) {
            throw new IllegalArgumentException("Candied light profile must target the attacking entity");
        }
        this.heavyConditions = heavyConditions;
        this.heavyInputs = heavyInputs;
        this.heavyOutput = heavyOutput.copy();
        this.heavyValues = Map.copyOf(heavyValues);
        this.heavyDesc = heavyDesc;
        this.heavySocketTypes = List.copyOf(heavySocketTypes);
        this.lightEffect = lightEffect;
        this.lightTarget = lightTarget;
        this.lightValues = Map.copyOf(lightValues);
        this.lightStackOnReapply = lightStackOnReapply;
        this.lightStackingLimit = lightStackingLimit;
        this.lightSocketTypes = List.copyOf(lightSocketTypes);
        this.heavy = new JeicyTieredDropTransformBonus(gemClass, heavyConditions, heavyInputs,
            heavyOutput, heavyValues, heavyDesc, heavySocketTypes);
        this.light = new JeicyTieredMobEffectBonus(gemClass, lightEffect, lightTarget,
            lightValues, lightStackOnReapply, lightStackingLimit, lightSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return heavy.matchesSocket(socketed) || light.matchesSocket(socketed);
    }

    @Override
    public void modifyLoot(GemInstance gem, ObjectArrayList<ItemStack> loot, LootContext context) {
        heavy.modifyLoot(gem, loot, context);
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

    Double heavyChanceForRank(OriginalGemRank rank) {
        return heavy.chanceForRank(rank);
    }

    boolean heavyAcceptsForVerification(ItemStack stack) {
        return heavy.acceptsForVerification(stack);
    }

    ItemStack heavyOutputForVerification() {
        return heavy.outputForVerification();
    }

    JeicyTieredMobEffectBonus.EffectData lightValueForRank(OriginalGemRank rank) {
        return light.valueForRank(rank);
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
