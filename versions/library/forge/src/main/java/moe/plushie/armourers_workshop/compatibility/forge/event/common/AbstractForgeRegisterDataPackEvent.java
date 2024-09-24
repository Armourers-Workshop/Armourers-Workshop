package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterDataPackEvent;

public class AbstractForgeRegisterDataPackEvent {

    public static IEventHandler<RegisterDataPackEvent> registryFactory() {
        return AbstractForgeCommonEventsImpl.DATA_PACK_REGISTRY.map(event -> event::addListener);
    }
}
