package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterEntityAttributesEvent;

public class AbstractForgeRegisterEntityAttributesEvent {

    public static IEventHandler<RegisterEntityAttributesEvent> registryFactory() {
        return AbstractForgeCommonEventsImpl.ENTITY_ATTRIBUTE_REGISTRY.map(event -> ((entity, builder) -> event.put(entity, builder.build())));
    }
}
