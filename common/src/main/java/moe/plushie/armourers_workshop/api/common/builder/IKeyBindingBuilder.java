package moe.plushie.armourers_workshop.api.common.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;

import java.util.function.Supplier;

public interface IKeyBindingBuilder<T extends IKeyBinding> {

    IKeyBindingBuilder<T> modifier(IKeyModifier modifier);

    IKeyBindingBuilder<T> category(String category);

    IKeyBindingBuilder<T> bind(Supplier<Runnable> handler);

    T build(String name);
}
