package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.platform.forge.EventManagerImpl;

@Available("[1.20, )")
public class AbstractForgeRegisterTextureEvent {

    public static IEventHandler<RegisterTextureEvent> registryFactory() {
        // everything in the block, item, particle and a few other folders is now stitched automaticall.
        return EventManagerImpl.placeholder(RegisterTextureEvent.class);
    }
}
