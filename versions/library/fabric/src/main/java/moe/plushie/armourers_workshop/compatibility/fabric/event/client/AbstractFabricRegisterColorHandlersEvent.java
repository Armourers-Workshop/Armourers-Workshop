package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterColorHandlersEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.EventManagerImpl;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

@Available("[1.16, )")
public class AbstractFabricRegisterColorHandlersEvent {

    public static IEventHandler<RegisterColorHandlersEvent.Block> blockFactory() {
        return EventManagerImpl.factory(() -> (provider, blocks) -> ColorProviderRegistry.BLOCK.register(provider::getTintColor, blocks));
    }

    public static IEventHandler<RegisterColorHandlersEvent.Item> itemFactory() {
        return EventManagerImpl.factory(() -> (provider, items) -> ColorProviderRegistry.ITEM.register(provider::getTintColor, items));
    }
}
