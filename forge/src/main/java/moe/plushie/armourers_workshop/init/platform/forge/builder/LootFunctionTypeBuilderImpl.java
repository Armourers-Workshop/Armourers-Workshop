package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractLootItemFunctionTypeBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;

public class LootFunctionTypeBuilderImpl<T extends ILootItemFunction> implements ILootFunctionTypeBuilder<T> {

    private final AbstractLootItemFunctionTypeBuilder<T> builder;

    public LootFunctionTypeBuilderImpl(IDataMapCodec<T> codec) {
        this.builder = new AbstractLootItemFunctionTypeBuilder<>(codec);
    }

    @Override
    public IRegistryHolder<ILootItemFunctionType<T>> build(String name) {
        return Registries.ITEM_LOOT_FUNCTIONS.register(name, builder::build);
    }
}
