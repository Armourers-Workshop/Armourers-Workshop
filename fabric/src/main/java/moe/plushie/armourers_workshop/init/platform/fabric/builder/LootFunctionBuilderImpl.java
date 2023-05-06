package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionBuilder;

import java.util.function.Supplier;

public class LootFunctionBuilderImpl<T extends ILootFunction> implements ILootFunctionBuilder<T> {

    private final Supplier<ILootFunction.Serializer<T>> serializer;

    public LootFunctionBuilderImpl(Supplier<ILootFunction.Serializer<T>> serializer) {
        this.serializer = serializer;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        return null;
    }
}
