package moe.plushie.armourers_workshop.compat.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeResourceLoader;
import moe.plushie.armourers_workshop.init.event.common.RegisterServerDataPackEvent;
import moe.plushie.armourers_workshop.init.ModConstants;

@Available("[1.16, 1.22)")
public class AbstractForgeRegisterServerDataPackEvent {

    public static IEventHandler<RegisterServerDataPackEvent> registryFactory() {
        return AbstractForgeCommonEventsImpl.DATA_PACK_REGISTRY.map(event -> loader -> {
            var name = ModConstants.key("custom-server-data-pack");
            event.addListener(new AbstractForgeResourceLoader(name, loader));
        });
    }
}
