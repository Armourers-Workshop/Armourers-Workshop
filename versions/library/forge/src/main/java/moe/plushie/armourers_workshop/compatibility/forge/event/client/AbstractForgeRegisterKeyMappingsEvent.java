package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterKeyMappingsEvent;

@Available("[1.19, )")
public class AbstractForgeRegisterKeyMappingsEvent {

    public static IEventHandler<RegisterKeyMappingsEvent> registryFactory() {
        return AbstractForgeClientEventsImpl.KEY_REGISTRY.map(event -> event::register);
    }
}
