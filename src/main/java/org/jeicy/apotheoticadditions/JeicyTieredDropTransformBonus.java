package org.jeicy.apotheoticadditions;

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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/** Drop transformation driven by the original nine-rank chance map. */
public final class JeicyTieredDropTransformBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<JeicyTieredDropTransformBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        ContextAwarePredicate.CODEC.fieldOf("conditions").forGetter(b -> b.conditions),
        Ingredient.CODEC_NONEMPTY.fieldOf("inputs").forGetter(b -> b.inputs),
        ItemStack.CODEC.fieldOf("output").forGetter(b -> b.output),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("values")
            .forGetter(b -> b.values.serialized()),
        Codec.STRING.fieldOf("desc").forGetter(b -> b.descKey),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(b -> b.originalSocketTypes)
    ).apply(inst, JeicyTieredDropTransformBonus::new));

    private final ContextAwarePredicate conditions;
    private final Ingredient inputs;
    private final ItemStack output;
    private final OriginalRankValues values;
    private final String descKey;
    private final List<String> originalSocketTypes;

    public JeicyTieredDropTransformBonus(GemClass gemClass, ContextAwarePredicate conditions,
                                         Ingredient inputs, ItemStack output,
                                         Map<String, Double> rawValues, String descKey,
                                         List<String> originalSocketTypes) {
        super(gemClass);
        if (output.isEmpty()) throw new IllegalArgumentException("Drop transform output cannot be empty");
        if (originalSocketTypes.isEmpty()) {
            throw new IllegalArgumentException("Original socket types cannot be empty");
        }
        if (rawValues.values().stream().anyMatch(value -> value < 0 || value > 1)) {
            throw new IllegalArgumentException("Drop transform chances must be between zero and one");
        }
        this.conditions = conditions;
        this.inputs = inputs;
        this.output = output.copy();
        this.values = new OriginalRankValues(rawValues);
        this.descKey = descKey;
        this.originalSocketTypes = List.copyOf(originalSocketTypes);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return OriginalSocketMatcher.matchesAny(socketed, originalSocketTypes);
    }

    @Override
    public void modifyLoot(GemInstance gem, ObjectArrayList<ItemStack> loot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool == null || !matchesSocket(tool) || !conditions.matches(context)) return;
        Double chance = values.get(JeicyGemRankAccess.get(gem.gemStack()));
        if (chance == null || context.getRandom().nextFloat() > chance) return;

        for (int index = 0; index < loot.size(); index++) {
            ItemStack stack = loot.get(index);
            if (inputs.test(stack)) {
                ItemStack replacement = output.copy();
                replacement.setCount(stack.getCount());
                loot.set(index, replacement);
            }
        }
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        Double chance = values.get(JeicyGemRankAccess.get(gem.gemStack()));
        if (chance == null) return Component.empty();
        return Component.translatable(descKey, Affix.fmt(chance.floatValue() * 100))
            .withStyle(ChatFormatting.YELLOW);
    }

    Double chanceForRank(OriginalGemRank rank) {
        return values.get(rank);
    }

    boolean acceptsForVerification(ItemStack stack) {
        return inputs.test(stack);
    }

    ItemStack outputForVerification() {
        return output.copy();
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
