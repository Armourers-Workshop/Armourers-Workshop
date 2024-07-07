package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterModelEvent;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

@Available("[1.21, )")
public class AbstractFabricRegisterModelEvent {

    public static IEventHandler<RegisterModelEvent> registryFactory() {
        return subscriber -> ModelLoadingPlugin.register(pluginContext -> subscriber.accept(registryName -> {
            pluginContext.addModels(ResourceLocation.create(registryName.getNamespace(), "item/" + registryName.getPath()));
        }));
    }
}
