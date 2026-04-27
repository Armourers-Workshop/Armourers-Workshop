package moe.plushie.armourers_workshop.compat.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.event.common.LauncherConfigSetupEvent;

@Available("[16, )")
public class AbstractForgeConfigEvent {

    public static IEventHandler<LauncherConfigSetupEvent> registryFactory() {
        return AbstractForgeCommonEventsImpl.FML_CONFIG.map(event -> () -> event.getConfig().getSpec());
    }
}
