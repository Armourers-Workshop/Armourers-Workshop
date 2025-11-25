package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IKeyMappingBuilder;
import moe.plushie.armourers_workshop.compat.fabric.builder.AbstractFabricKeyMappingBuilder;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

import java.util.function.Supplier;

public class KeyMappingBuilderImpl<T extends IKeyMapping> implements IKeyMappingBuilder<T> {

    private final AbstractFabricKeyMappingBuilder<T> builder;

    public KeyMappingBuilderImpl(String key, IKeyCategory category) {
        this.builder = new AbstractFabricKeyMappingBuilder<>(key, category);
    }

    @Override
    public IKeyMappingBuilder<T> modifier(IKeyModifier modifier) {
        this.builder.modifier(modifier);
        return this;
    }

    @Override
    public IKeyMappingBuilder<T> bind(Supplier<Runnable> handler) {
        this.builder.bind(handler);
        return this;
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        return ClientRegistries.KEY_BINDINGS.register(name, builder::build);
    }
}
