package moe.plushie.armourers_workshop.compat.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.event.common.ServerLevelAddEntityEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerLevelTickEvent;
import net.minecraft.server.level.ServerLevel;

@Available("[21, )")
public class AbstractForgeServerLevelEvent {

    public static IEventHandler<ServerLevelTickEvent.Pre> preTickFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_LEVEL_TICK_PRE.flatMap(event -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                return () -> serverLevel;
            }
            return null;
        });
    }

    public static IEventHandler<ServerLevelTickEvent.Post> postTickFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_LEVEL_TICK_POST.flatMap(event -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                return () -> serverLevel;
            }
            return null;
        });
    }

    public static IEventHandler<ServerLevelAddEntityEvent> addEntityFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_LEVEL_ADD_ENTITY.flatMap(event -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                return event::getEntity;
            }
            return null;
        });
    }
}
