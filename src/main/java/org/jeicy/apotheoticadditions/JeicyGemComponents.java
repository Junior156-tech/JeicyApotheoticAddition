package org.jeicy.apotheoticadditions;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Real persisted and network-synchronized original-tier data component.
 * This alone does not change Apotheosis' bonus calculations: a separate hook
 * must consume the value rather than interpreting it as Purity.PERFECT.
 */
public final class JeicyGemComponents {
    private static final DeferredRegister.DataComponents REGISTRAR =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "apotheotic_additions");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ORIGINAL_GEM_RANK =
        REGISTRAR.registerComponentType("original_gem_rank", builder -> builder
            .persistent(Codec.intRange(0, OriginalGemRank.ESOTERIC.stableId()))
            .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static void register(IEventBus bus) { REGISTRAR.register(bus); }

    private JeicyGemComponents() {}
}
