package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class EventManager {

    private static final HashMap<Class<?>, IEventHandler<?>> SOURCES = new HashMap<>();
    private static final HashMap<Class<?>, ArrayList<Consumer<?>>> HANDLERS = new HashMap<>();

    public static <E> void listen(Class<E> eventType, Consumer<E> subscriber) {
        listen(eventType, IEventHandler.Priority.NORMAL, false, subscriber);
    }

    public static <E> void listen(Class<E> eventType, IEventHandler.Priority priority, boolean receiveCancelled, Consumer<E> subscriber) {
        var handler = SOURCES.get(eventType);
        if (handler != null) {
            handler.listen(priority, receiveCancelled, Objects.unsafeCast(subscriber));
        }
        // save it to custom post.
        HANDLERS.computeIfAbsent(eventType, key -> new ArrayList<>()).add(subscriber);
    }

    public static <E> void post(Class<? super E> eventType, E event) {
        var handlers = HANDLERS.get(eventType);
        if (handlers != null) {
            handlers.forEach(it -> it.accept(Objects.unsafeCast(event)));
        }
    }

    public static <E> void post(Class<? super E> eventType, IEventHandler<E> handler) {
        SOURCES.put(eventType, handler);
    }

    protected abstract void init();

    static {
        PlatformLoader.load(EventManager.class).init();
        EventBus.init();
    }
}
