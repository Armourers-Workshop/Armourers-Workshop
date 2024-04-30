package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterCommandsEvent;

public class AbstractForgeRegisterCommandsEvent {

    public static IEventHandler<RegisterCommandsEvent> registryFactory() {
        return AbstractForgeCommonEventsImpl.COMMAND_REGISTRY.map(event -> event.getDispatcher()::register);
    }
}
