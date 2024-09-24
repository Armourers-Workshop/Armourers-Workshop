package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerTickEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Available("[1.16, )")
public class AbstractFabricServerTickEvent {

    public static IEventHandler<ServerTickEvent.Pre> preTickFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerTickEvents.START_SERVER_TICK.register(server -> subscriber.accept(() -> server));
    }

    public static IEventHandler<ServerTickEvent.Post> postTickFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerTickEvents.END_SERVER_TICK.register(server -> subscriber.accept(() -> server));
    }
}
