package org.jeicy.apotheoticadditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.affix.ItemAffixes;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.LootRule;
import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;

/**
 * Executes the complete 1.20.1 rarity rule sequence as one 1.21.1 loot rule.
 *
 * <p>This is deliberately not expressed with Apotheosis' built-in 1.21 rules:
 * its socket rule sets an absolute socket count instead of incrementing a
 * counter, its durability rule samples a continuous range instead of the
 * original fifteen values, and its affix rule uses weighted selection. Keeping
 * the sequence together also preserves the original random-call order.</p>
 */
public record JeicyOriginalRarityLootRule(
    List<Float> removedAffixChances,
    List<AffixRoll> affixes,
    List<Float> socketChances,
    Optional<Float> durability
) implements LootRule {

    private static final Codec<Float> CHANCE_CODEC = Codec.floatRange(0.0F, 1.0F);

    public static final Codec<JeicyOriginalRarityLootRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        CHANCE_CODEC.listOf().optionalFieldOf("removed_affix_chances", List.of())
            .forGetter(JeicyOriginalRarityLootRule::removedAffixChances),
        AffixRoll.CODEC.listOf().fieldOf("affixes")
            .forGetter(JeicyOriginalRarityLootRule::affixes),
        CHANCE_CODEC.listOf().fieldOf("socket_chances")
            .forGetter(JeicyOriginalRarityLootRule::socketChances),
        CHANCE_CODEC.optionalFieldOf("durability")
            .forGetter(JeicyOriginalRarityLootRule::durability)
    ).apply(instance, JeicyOriginalRarityLootRule::new));

    public JeicyOriginalRarityLootRule {
        removedAffixChances = List.copyOf(removedAffixChances);
        affixes = List.copyOf(affixes);
        socketChances = List.copyOf(socketChances);
    }

    @Override
    public Codec<? extends LootRule> getCodec() {
        return CODEC;
    }

    @Override
    public void execute(ItemStack stack, LootRarity rarity, GenContext context) {
        Set<DynamicHolder<Affix>> selected = new LinkedHashSet<>();

        // Apotheosis 8 removed AffixType.ANCIENT. Apotheosis 7 shipped no
        // built-in affixes of that type, so its original rule consumed its
        // chance roll and selected nothing. Preserve that observable behavior.
        for (float chance : this.removedAffixChances) {
            passes(context, chance);
        }

        for (AffixRoll roll : this.affixes) {
            select(stack, rarity, context, selected, roll);
        }

        ItemAffixes.Builder builder = ItemAffixes.EMPTY.toBuilder();
        for (DynamicHolder<Affix> affix : selected) {
            builder.put(affix, context.rand().nextFloat());
        }
        AffixHelper.setAffixes(stack, builder.build());

        int sockets = 0;
        for (float chance : this.socketChances) {
            if (passes(context, chance)) sockets++;
        }
        if (sockets > 0) {
            SocketHelper.setSockets(stack, Math.max(SocketHelper.getSockets(stack), sockets));
        }

        this.durability.ifPresent(base -> stack.set(
            Apoth.Components.DURABILITY_BONUS,
            OriginalRarityMath.steppedDurability(base, context.rand().nextFloat())));
    }

    private static void select(ItemStack stack, LootRarity rarity, GenContext context,
        Set<DynamicHolder<Affix>> selected, AffixRoll roll) {
        if (!passes(context, roll.chance())) return;
        if (selectOne(stack, rarity, context, selected, roll.type())) return;
        if (roll.backupType().isPresent() && passes(context, roll.backupChance())) {
            selectOne(stack, rarity, context, selected, roll.backupType().orElseThrow());
        }
    }

    private static boolean selectOne(ItemStack stack, LootRarity rarity, GenContext context,
        Set<DynamicHolder<Affix>> selected, AffixType type) {
        List<DynamicHolder<Affix>> available = new ArrayList<>(LootController
            .getAvailableAffixes(stack, rarity, type)
            .filter(holder -> !selected.contains(holder))
            .toList());
        if (available.isEmpty()) return false;

        Random legacyShuffle = new Random(context.rand().nextLong());
        Collections.shuffle(available, legacyShuffle);
        selected.add(available.getFirst());
        return true;
    }

    static boolean passes(GenContext context, float chance) {
        return context.rand().nextFloat() <= chance;
    }

    public record AffixRoll(
        AffixType type,
        float chance,
        Optional<AffixType> backupType,
        float backupChance
    ) {
        public static final Codec<AffixRoll> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AffixType.CODEC.fieldOf("affix_type").forGetter(AffixRoll::type),
            CHANCE_CODEC.optionalFieldOf("chance", 1.0F).forGetter(AffixRoll::chance),
            AffixType.CODEC.optionalFieldOf("backup_affix_type").forGetter(AffixRoll::backupType),
            CHANCE_CODEC.optionalFieldOf("backup_chance", 1.0F).forGetter(AffixRoll::backupChance)
        ).apply(instance, AffixRoll::new));
    }
}
