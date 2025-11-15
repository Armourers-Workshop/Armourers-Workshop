package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricResourceLoader;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.client.RegisterClientDataPackEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Available("[1.16, )")
public class AbstractFabricRegisterClientDataPackEvent {

    public static IEventHandler<RegisterClientDataPackEvent> registryFactory() {
        return (priority, receiveCancelled, subscriber) -> {
            EnvironmentExecutor.willSetup(EnvironmentType.CLIENT, () -> () -> {
                subscriber.accept(loader -> {
                    var name = ModConstants.key("custom-client-data-pack").toLocation();
                    var resourceLoader = new AbstractFabricResourceLoader(name, loader);
                    ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(resourceLoader);
                });
            });
        };
    }
}
