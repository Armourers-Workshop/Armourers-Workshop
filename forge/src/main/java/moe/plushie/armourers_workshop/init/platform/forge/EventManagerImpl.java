package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEvents;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeCommonEvents;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.EventManager;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class EventManagerImpl extends EventManager {

    @Override
    protected void init() {
        AbstractForgeCommonEvents.init();
        EnvironmentExecutor.runOnClient(() -> AbstractForgeClientEvents::init);
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
