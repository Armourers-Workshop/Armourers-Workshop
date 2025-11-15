package moe.plushie.armourers_workshop.compat.forge.event.common;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.event.common.RegisterEntityAttributesEvent;

public class AbstractForgeRegisterEntityAttributesEvent {

    public static IEventHandler<RegisterEntityAttributesEvent> registryFactory() {
        return AbstractForgeCommonEventsImpl.ENTITY_ATTRIBUTE_REGISTRY.map(event -> ((entity, builder) -> event.put(entity, builder.build())));
    }
}
