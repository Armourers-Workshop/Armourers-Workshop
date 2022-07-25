package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.key.IKeyModifier;

import java.util.function.Supplier;

public interface IKeyBindingBuilder<T extends IKeyBinding> {

    IKeyBindingBuilder<T> modifier(IKeyModifier modifier);

    IKeyBindingBuilder<T> category(String category);

    IKeyBindingBuilder<T> bind(Supplier<Runnable> handler);

    T build(String name);
}
