package moe.plushie.armourers_workshop.compat.fabric.core.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.common.ServerLevelAddEntityEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerLevelTickEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Available("[16, 26)")
public class AbstractFabricServerLevelEvent {

    public static IEventHandler<ServerLevelTickEvent.Pre> preTickFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerTickEvents.START_WORLD_TICK.register(level -> subscriber.accept(() -> level));
    }

    public static IEventHandler<ServerLevelTickEvent.Post> postTickFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerTickEvents.END_WORLD_TICK.register(level -> subscriber.accept(() -> level));
    }

    public static IEventHandler<ServerLevelAddEntityEvent> addEntityFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> subscriber.accept(() -> entity));
    }
}
