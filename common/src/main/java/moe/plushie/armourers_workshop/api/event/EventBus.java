package moe.plushie.armourers_workshop.api.event;

import moe.plushie.armourers_workshop.init.platform.EventManager;

import java.util.function.Consumer;

public final class EventBus {

    public static <E> void register(Class<E> eventClass, Consumer<E> eventHandler) {
        EventManager.listen(eventClass, eventHandler);
    }

    public static <E> void post(Class<E> eventClass, E event) {
        EventManager.post(eventClass, event);
    }

    public static void init() {
    }
}
