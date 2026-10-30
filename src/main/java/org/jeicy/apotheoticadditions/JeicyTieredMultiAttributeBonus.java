package org.jeicy.apotheoticadditions;

import java.util.List;
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
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

/**
 * Multi-attribute bonus with separate exact 1.20 socket profiles. Profiles are
 * necessary when 1.21 maps sword and heavy-weapon bonuses to the same category.
 */
public final class JeicyTieredMultiAttributeBonus extends GemBonus implements JeicyExactSocketBonus {
    public static final Codec<Modifier> MODIFIER_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute")
            .forGetter(m -> m.attribute),
        PlaceboCodecs.enumCodec(Operation.class).fieldOf("operation")
            .forGetter(m -> m.operation),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("values")
            .forGetter(m -> m.values.serialized())
    ).apply(inst, Modifier::new));

    public static final Codec<Profile> PROFILE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.STRING.fieldOf("key").forGetter(p -> p.key),
        Codec.STRING.listOf().fieldOf("original_socket_types")
            .forGetter(p -> p.originalSocketTypes),
        MODIFIER_CODEC.listOf().fieldOf("modifiers").forGetter(p -> p.modifiers)
    ).apply(inst, Profile::new));

    public static final Codec<JeicyTieredMultiAttributeBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        gemClass(),
        PROFILE_CODEC.listOf().fieldOf("profiles").forGetter(b -> b.profiles)
    ).apply(inst, JeicyTieredMultiAttributeBonus::new));

    private final List<Profile> profiles;

    public JeicyTieredMultiAttributeBonus(GemClass gemClass, List<Profile> profiles) {
        super(gemClass);
        if (profiles.isEmpty()) throw new IllegalArgumentException("At least one profile is required");
        this.profiles = List.copyOf(profiles);
    }

    @Override
    public boolean matchesSocket(ItemStack socketed) {
        return selectProfile(socketed) != null;
    }

    private Profile selectProfile(ItemStack socketed) {
        for (Profile profile : profiles) {
            if (OriginalSocketMatcher.matchesAny(socketed, profile.originalSocketTypes)) return profile;
        }
        return null;
    }

    int profileIndexFor(ItemStack socketed) {
        Profile profile = selectProfile(socketed);
        return profile == null ? -1 : profiles.indexOf(profile);
    }

    Double valueForRank(int profileIndex, int modifierIndex, OriginalGemRank rank) {
        return profiles.get(profileIndex).modifiers.get(modifierIndex).values.get(rank);
    }

    private OriginalGemRank originalRank(GemView gem) {
        return JeicyGemRankAccess.get(gem.gemStack());
    }

    @Override
    public boolean supports(Purity purity) {
        return true;
    }

    @Override
    public void addModifiers(GemInstance gem, StackAttributeModifiersEvent event) {
        Profile profile = selectProfile(event.getItemStack());
        OriginalGemRank rank = originalRank(gem);
        if (profile == null || rank == null) return;

        int profileIndex = profiles.indexOf(profile);
        for (int modifierIndex = 0; modifierIndex < profile.modifiers.size(); modifierIndex++) {
            Modifier modifier = profile.modifiers.get(modifierIndex);
            Double value = modifier.values.get(rank);
            if (value == null) continue;
            ResourceLocation id = makeUniqueId(gem, profileIndex + "_" + modifierIndex);
            event.addModifier(modifier.attribute,
                new AttributeModifier(id, value, modifier.operation), gem.category().getSlots());
        }
    }

    @Override
    public void skipModifierIds(GemInstance gem, Consumer<ResourceLocation> skip) {
        for (int profileIndex = 0; profileIndex < profiles.size(); profileIndex++) {
            Profile profile = profiles.get(profileIndex);
            for (int modifierIndex = 0; modifierIndex < profile.modifiers.size(); modifierIndex++) {
                skip.accept(makeUniqueId(gem, profileIndex + "_" + modifierIndex));
            }
        }
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        OriginalGemRank rank = originalRank(gem);
        if (rank == null) return Component.empty();

        MutableComponent result = Component.empty();
        boolean firstProfile = true;
        for (int profileIndex = 0; profileIndex < profiles.size(); profileIndex++) {
            Profile profile = profiles.get(profileIndex);
            if (!firstProfile) result.append(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY));
            firstProfile = false;
            result.append(Component.literal(profile.key + ": ").withStyle(ChatFormatting.YELLOW));
            for (int modifierIndex = 0; modifierIndex < profile.modifiers.size(); modifierIndex++) {
                Modifier modifier = profile.modifiers.get(modifierIndex);
                Double value = modifier.values.get(rank);
                if (value == null) continue;
                if (modifierIndex > 0) result.append(Component.literal(", "));
                result.append(modifier.attribute.value().toComponent(
                    new AttributeModifier(makeUniqueId(gem, profileIndex + "_" + modifierIndex),
                        value, modifier.operation), ctx.flag()));
            }
        }
        return result;
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    public static final class Modifier {
        private final Holder<Attribute> attribute;
        private final Operation operation;
        private final OriginalRankValues values;

        public Modifier(Holder<Attribute> attribute, Operation operation,
                        Map<String, Double> rawValues) {
            this.attribute = attribute;
            this.operation = operation;
            this.values = new OriginalRankValues(rawValues);
        }
    }

    public static final class Profile {
        private final String key;
        private final List<String> originalSocketTypes;
        private final List<Modifier> modifiers;

        public Profile(String key, List<String> originalSocketTypes, List<Modifier> modifiers) {
            if (originalSocketTypes.isEmpty()) {
                throw new IllegalArgumentException("Original socket types cannot be empty");
            }
            if (modifiers.isEmpty()) throw new IllegalArgumentException("Modifiers cannot be empty");
            this.key = key;
            this.originalSocketTypes = List.copyOf(originalSocketTypes);
            this.modifiers = List.copyOf(modifiers);
        }
    }
}
