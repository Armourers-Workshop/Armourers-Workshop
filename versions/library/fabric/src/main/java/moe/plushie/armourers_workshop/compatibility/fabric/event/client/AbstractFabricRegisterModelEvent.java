package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterModelEvent;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

@Available("[1.16, )")
public class AbstractFabricRegisterModelEvent {

    public static IEventHandler<RegisterModelEvent> registryFactory() {
        return subscriber -> ModelLoadingRegistry.INSTANCE.registerModelProvider((resourceManager, registry) -> subscriber.accept(registry::accept));
    }

}
