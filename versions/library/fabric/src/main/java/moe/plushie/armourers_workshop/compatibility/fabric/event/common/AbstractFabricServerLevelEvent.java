package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerLevelAddEntityEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerLevelTickEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Available("[1.16, )")
public class AbstractFabricServerLevelEvent {

    public static IEventHandler<ServerLevelTickEvent> startTickFactory() {
        return subscriber -> ServerTickEvents.START_WORLD_TICK.register(level -> subscriber.accept(() -> level));
    }

    public static IEventHandler<ServerLevelAddEntityEvent> addEntityFactory() {
        return subscriber -> ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> subscriber.accept(() -> entity));
    }
}
