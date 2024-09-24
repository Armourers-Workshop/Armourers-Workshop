package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricClientEvents;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricCommonEvents;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;

import java.util.function.Supplier;

public class EventManagerImpl {

    public static void init() {
        AbstractFabricCommonEvents.init();
        EnvironmentExecutor.runOn(EnvironmentType.CLIENT, () -> AbstractFabricClientEvents::init);
    }

    public static <E> IEventHandler<E> factory(Supplier<E> factory) {
        return (priority, receiveCancelled, handler) -> handler.accept(factory.get());
    }

    public static <E> IEventHandler<E> placeholder(Class<E> type) {
        return (priority, receiveCancelled, handler) -> {
            // ignore
        };
    }
}
