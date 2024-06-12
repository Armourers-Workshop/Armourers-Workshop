package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterModelEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

@Available("[1.21, )")
public class AbstractForgeRegisterModelEvent {

    public static IEventHandler<RegisterModelEvent> registryFactory() {
        return AbstractForgeClientEventsImpl.MODEL_REGISTRY.map(event -> registryName -> {
            ResourceLocation location = ResourceLocation.create(registryName.getNamespace(), "item/" + registryName.getPath());
            event.register(ModelResourceLocation.standalone(location));
        });
    }
}
