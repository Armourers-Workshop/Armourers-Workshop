package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IKeyMappingBuilder<T extends IKeyMapping> extends IRegistryBuilder<T> {

    IKeyMappingBuilder<T> modifier(IKeyModifier modifier);

    IKeyMappingBuilder<T> bind(Supplier<Runnable> handler);
}
