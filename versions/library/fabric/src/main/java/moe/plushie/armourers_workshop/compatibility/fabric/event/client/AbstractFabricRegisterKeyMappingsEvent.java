package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterKeyMappingsEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.EventManagerImpl;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

@Available("[1.16, )")
public class AbstractFabricRegisterKeyMappingsEvent {

    public static IEventHandler<RegisterKeyMappingsEvent> registryFactory() {
        return EventManagerImpl.factory(() -> KeyBindingHelper::registerKeyBinding);
    }

}
