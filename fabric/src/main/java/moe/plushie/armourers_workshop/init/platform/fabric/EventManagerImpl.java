package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.fabric.client.AbstractFabricClientEvents;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricCommonEvents;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.EventManager;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class EventManagerImpl extends EventManager {

    @Override
    protected void init() {
        AbstractFabricCommonEvents.init();
        EnvironmentExecutor.runOnClient(() -> AbstractFabricClientEvents::init);
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
