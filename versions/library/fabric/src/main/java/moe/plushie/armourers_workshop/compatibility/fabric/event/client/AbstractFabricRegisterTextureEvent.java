package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.EventManagerImpl;

@Available("[1.20, )")
public class AbstractFabricRegisterTextureEvent {

    public static IEventHandler<RegisterTextureEvent> registryFactory() {
        // everything in the block, item, particle and a few other folders is now stitched automaticall.
        return EventManagerImpl.placeholder(RegisterTextureEvent.class);
    }
}
