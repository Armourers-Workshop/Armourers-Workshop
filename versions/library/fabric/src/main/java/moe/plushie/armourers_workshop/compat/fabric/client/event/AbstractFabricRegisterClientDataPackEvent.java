package moe.plushie.armourers_workshop.compat.fabric.client.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricPreparableReloadListener;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.client.RegisterClientDataPackEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Available("[16, )")
public class AbstractFabricRegisterClientDataPackEvent {

    public static IEventHandler<RegisterClientDataPackEvent> registryFactory() {
        return (priority, receiveCancelled, subscriber) -> {
            EnvironmentExecutor.willSetup(EnvironmentType.CLIENT, () -> () -> {
                subscriber.accept(listener -> {
                    var name = ModConstants.key("custom-client-data-pack");
                    var listener1 = new AbstractFabricPreparableReloadListener(name, listener);
                    ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(listener1);
                });
            });
        };
    }
}
