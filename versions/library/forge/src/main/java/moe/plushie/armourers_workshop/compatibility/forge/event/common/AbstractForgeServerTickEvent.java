package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerTickEvent;

@Available("[1.21, )")
public class AbstractForgeServerTickEvent {

    public static IEventHandler<ServerTickEvent.Pre> preTickFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_TICK_PRE.map(event -> event::getServer);
    }

    public static IEventHandler<ServerTickEvent.Post> postTickFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_TICK_POST.map(event -> event::getServer);
    }
}
