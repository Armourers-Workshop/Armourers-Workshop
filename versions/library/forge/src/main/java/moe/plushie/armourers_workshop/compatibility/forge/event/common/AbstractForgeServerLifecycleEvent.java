package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartedEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppingEvent;

@Available("[1.16, )")
public class AbstractForgeServerLifecycleEvent {

    public static IEventHandler<ServerStartingEvent> aboutToStartFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_WILL_START.map(event -> event::getServer);
    }

    public static IEventHandler<ServerStartedEvent> startedFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_DID_START.map(event -> event::getServer);
    }

    public static IEventHandler<ServerStoppingEvent> stoppingFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_WILL_STOP.map(event -> event::getServer);
    }

    public static IEventHandler<ServerStoppedEvent> stoppedFactory() {
        return AbstractForgeCommonEventsImpl.SERVER_DID_STOP.map(event -> event::getServer);
    }
}
