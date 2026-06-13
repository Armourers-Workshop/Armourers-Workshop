package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractSpecialModelRendererBuilder;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class SpecialModelRendererBuilderImpl<T extends SpecialModelRenderer<?>> implements IRegistryBuilder<SpecialModelRendererType<T>> {

    private final AbstractSpecialModelRendererBuilder<T> builder;

    public SpecialModelRendererBuilderImpl(IDataMapCodec<T> codec) {
        this.builder = new AbstractSpecialModelRendererBuilder<>(codec);
    }

    @Override
    public IRegistryHolder<SpecialModelRendererType<T>> build(String name) {
        return ClientRegistries.SPECIAL_MODEL_RENDERER_TYPES.register(name, builder::build);
    }
}

