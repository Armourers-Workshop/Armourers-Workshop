package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class NotificationCenterImpl {

    private static final HashMap<Class<?>, ArrayList<?>> LISTENERS = new HashMap<>();

    public static <E extends Event> void observer(Class<E> clazz, Consumer<E> handler) {
        observer(clazz, handler, event -> event);
    }

    public static <E extends Event, T> void observer(Class<E> clazz, Consumer<T> handler, Function<E, T> transform) {
        ArrayList<Consumer<T>> handlers = ObjectUtils.unsafeCast(LISTENERS.computeIfAbsent(clazz, key -> {
            ArrayList<Consumer<T>> queue = new ArrayList<>();
            Consumer<E> listener = event -> queue.forEach(element -> element.accept(transform.apply(event)));
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, clazz, listener);
            if (MinecraftForge.getModEventBusClass().isAssignableFrom(clazz)) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, clazz, listener);
            }
            return queue;
        }));
        handlers.add(handler);
    }

    public static <E extends GenericEvent<? extends F>, F> void observer(Class<E> clazz, Class<F> genericClazz, Consumer<E> handler) {
        observer(clazz, genericClazz, handler, event -> event);
    }

    public static <E extends GenericEvent<? extends F>, F, T> void observer(Class<E> clazz, Class<F> genericClazz, Consumer<T> handler, Function<E, T> transform) {
        ArrayList<Consumer<T>> handlers = ObjectUtils.unsafeCast(LISTENERS.computeIfAbsent(clazz, key -> {
            ArrayList<Consumer<T>> queue = new ArrayList<>();
            Consumer<E> listener = event -> queue.forEach(element -> element.accept(transform.apply(event)));
            MinecraftForge.EVENT_BUS.addGenericListener(genericClazz, EventPriority.NORMAL, false, clazz, listener);
            if (MinecraftForge.getModEventBusClass().isAssignableFrom(clazz)) {
                FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(genericClazz, EventPriority.NORMAL, false, clazz, listener);
            }
            return queue;
        }));
        handlers.add(handler);
    }
}
