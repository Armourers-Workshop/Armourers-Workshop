package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.common.IDataComponentType;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataComponentType;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.TypedRegistry;

public class DataComponentTypeBuilderImpl<T> implements IDataComponentTypeBuilder<T> {

    private final Codec<T> codec;
    private String tag;

    public DataComponentTypeBuilderImpl(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public IDataComponentTypeBuilder<T> tag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public IRegistryHolder<IDataComponentType<T>> build(String name) {
        AbstractDataComponentType<T> proxy = AbstractDataComponentType.create(tag, codec);
        AbstractForgeRegistries.DATA_COMPONENT_TYPES.register(name, () -> proxy);
        return TypedRegistry.Entry.of(ModConstants.key(name), () -> proxy);
    }
}
