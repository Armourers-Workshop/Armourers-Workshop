package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.common.IDataComponentType;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataComponentType;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistries;
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
    public IRegistryKey<IDataComponentType<T>> build(String name) {
        AbstractDataComponentType<T> proxy = AbstractDataComponentType.create(tag, codec);
        AbstractFabricRegistries.DATA_COMPONENT_TYPES.register(name, () -> proxy);
        return TypedRegistry.Entry.of(ModConstants.key(name), () -> proxy);
    }
}
