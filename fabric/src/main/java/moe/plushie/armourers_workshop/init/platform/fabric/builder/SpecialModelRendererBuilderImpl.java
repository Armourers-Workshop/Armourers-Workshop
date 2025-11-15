package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ISpecialModelRendererBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractSpecialModelRendererBuilder;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class SpecialModelRendererBuilderImpl<T extends ISpecialModelRenderer<?>> implements ISpecialModelRendererBuilder<T> {

    private final AbstractSpecialModelRendererBuilder<T> builder;

    public SpecialModelRendererBuilderImpl(IDataMapCodec<T> codec) {
        this.builder = new AbstractSpecialModelRendererBuilder<>(codec);
    }

    @Override
    public IRegistryHolder<ISpecialModelRendererType<T>> build(String name) {
        return ClientRegistries.SPECIAL_MODEL_RENDERER_TYPES.register(name, builder::build);
    }
}

