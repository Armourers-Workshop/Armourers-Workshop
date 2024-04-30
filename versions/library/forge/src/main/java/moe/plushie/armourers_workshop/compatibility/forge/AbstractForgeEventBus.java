package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@Available("[1.21, )")
public class AbstractForgeEventBus {


    private static final HashMap<Class<?>, ArrayList<?>> LISTENERS = new HashMap<>();

    public static <E extends net.neoforged.bus.api.Event> void observer(Class<E> eventType, Consumer<E> handler) {
        observer(eventType, handler, event -> event);
    }

    public static <E extends net.neoforged.bus.api.Event, T> void observer(Class<E> eventType, Consumer<T> handler, Function<E, T> transform) {
        ArrayList<Consumer<T>> handlers = ObjectUtils.unsafeCast(LISTENERS.computeIfAbsent(eventType, key -> {
            ArrayList<Consumer<T>> queue = new ArrayList<>();
            Consumer<E> listener = event -> queue.forEach(element -> element.accept(transform.apply(event)));
            if (IModBusEvent.class.isAssignableFrom(eventType)) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, eventType, listener);
            } else {
                NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventType, listener);
            }
            return queue;
        }));
        handlers.add(handler);
    }

    public static <E extends Event> IEventHandler<E> create(Class<E> type) {
        return handler -> AbstractForgeEventBus.observer(type, handler);
    }
}
