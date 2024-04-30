package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEvents;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEvents;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;

import java.util.Objects;
import java.util.function.Supplier;

public class EventManagerImpl {

    public static void init() {
        AbstractForgeCommonEvents.init();
        EnvironmentExecutor.runOn(EnvironmentType.CLIENT, () -> AbstractForgeClientEvents::init);
    }

    public static <E> IEventHandler<E> factory(Supplier<E> factory) {
        return handler -> handler.accept(factory.get());
    }

    public static <E> IEventHandler<E> placeholder(Class<E> type) {
        return Objects::hash;
    }
}
