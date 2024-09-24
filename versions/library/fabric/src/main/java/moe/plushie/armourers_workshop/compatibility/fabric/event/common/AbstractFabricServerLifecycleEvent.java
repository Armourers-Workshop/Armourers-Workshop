package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartedEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppingEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Available("[1.16, )")
public class AbstractFabricServerLifecycleEvent {

    public static IEventHandler<ServerStartingEvent> aboutToStartFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerLifecycleEvents.SERVER_STARTING.register(server -> subscriber.accept(() -> server));
    }

    public static IEventHandler<ServerStartedEvent> startedFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerLifecycleEvents.SERVER_STARTED.register(server -> subscriber.accept(() -> server));
    }

    public static IEventHandler<ServerStoppingEvent> stoppingFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerLifecycleEvents.SERVER_STOPPING.register(server -> subscriber.accept(() -> server));
    }

    public static IEventHandler<ServerStoppedEvent> stoppedFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerLifecycleEvents.SERVER_STOPPED.register(server -> subscriber.accept(() -> server));
    }
}
