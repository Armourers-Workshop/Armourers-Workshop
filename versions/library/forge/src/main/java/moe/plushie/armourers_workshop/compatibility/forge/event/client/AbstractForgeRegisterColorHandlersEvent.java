package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterColorHandlersEvent;

@Available("[1.16, )")
public class AbstractForgeRegisterColorHandlersEvent {

    public static IEventHandler<RegisterColorHandlersEvent.Block> blockFactory() {
        return AbstractForgeClientEventsImpl.BLOCK_COLOR_REGISTRY.map(event -> (provider, blocks) -> event.getBlockColors().register(provider::getTintColor, blocks));
    }

    public static IEventHandler<RegisterColorHandlersEvent.Item> itemFactory() {
        return AbstractForgeClientEventsImpl.ITEM_COLOR_REGISTRY.map(event -> (provider, items) -> event.getItemColors().register(provider::getTintColor, items));
    }
}
