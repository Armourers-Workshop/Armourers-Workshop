package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterEntityAttributesEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.EventManagerImpl;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

@Available("[1.16, )")
public class AbstractFabricRegisterEntityAttributesEvent {

    public static IEventHandler<RegisterEntityAttributesEvent> registryFactory() {
        return EventManagerImpl.factory(() -> FabricDefaultAttributeRegistry::register);
    }
}

