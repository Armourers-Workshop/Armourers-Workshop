package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class AbstractKeyMappingBuilder<T extends IKeyMapping> {

    private static final ArrayList<Pair<IKeyMapping, Supplier<Runnable>>> inputs = new ArrayList<>();

    protected Supplier<Runnable> handler;

    protected final String key;
    protected final IKeyCategory category;
    protected final ArrayList<IKeyModifier> modifiers = new ArrayList<>();

    public AbstractKeyMappingBuilder(String key, IKeyCategory category) {
        this.key = key;
        this.category = category;
    }

    public void modifier(IKeyModifier modifier) {
        this.modifiers.add(modifier);
    }

    public void bind(Supplier<Runnable> handler) {
        this.handler = handler;
    }

    public T build(OpenResourceLocation registryName) {
        var keyMapping = create(registryName);
        Objects.flatMap(handler, it -> inputs.add(Pair.of(keyMapping, it)));
        return Objects.unsafeCast(keyMapping);
    }

    protected abstract IKeyMapping create(OpenResourceLocation registryName);

    static {
        // attach the input event to client end frame.
        EventBus.register(RenderFrameEvent.Post.class, event -> inputs.forEach(pair -> {
            if (pair.getKey().consumeClick()) {
                pair.getValue().get().run();
            }
        }));
    }
}
