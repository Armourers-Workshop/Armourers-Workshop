package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;

public class DataComponentTypeBuilderImpl<T> implements IDataComponentTypeBuilder<T> {

    private final AbstractDataComponentTypeBuilder<T> builder;

    public DataComponentTypeBuilderImpl(IDataCodec<T> codec) {
        this.builder = new AbstractDataComponentTypeBuilder<T>(codec);
    }

    @Override
    public IDataComponentTypeBuilder<T> tag(String tag) {
        this.builder.tag(tag);
        return this;
    }

    @Override
    public IRegistryHolder<IDataComponentType<T>> build(String name) {
        return Registries.DATA_COMPONENT_TYPES.register(name, builder::build);
    }
}
