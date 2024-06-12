package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterItemPropertyEvent;
import net.minecraft.client.renderer.item.ItemProperties;

@Available("[1.18, )")
public class AbstractForgeRegisterItemPropertyEvent {

    public static IEventHandler<RegisterItemPropertyEvent> propertyFactory() {
        return AbstractForgeCommonEventsImpl.FML_LOAD_COMPLETE.map(event -> (registryName, item, property) -> {
            // forward
            ItemProperties.register(item, registryName.toLocation(), property::getValue);
        });
    }
}
