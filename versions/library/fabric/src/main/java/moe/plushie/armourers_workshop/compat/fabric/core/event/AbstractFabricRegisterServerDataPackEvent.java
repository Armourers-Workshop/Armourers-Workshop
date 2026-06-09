package moe.plushie.armourers_workshop.compat.fabric.core.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricPreparableReloadListener;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.event.common.RegisterServerDataPackEvent;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

@Available("[16, )")
public class AbstractFabricRegisterServerDataPackEvent {

    public static IEventHandler<RegisterServerDataPackEvent> registryFactory() {
        return (priority, receiveCancelled, subscriber) -> subscriber.accept(loader -> {
            var name = ModConstants.key("custom-server-data-pack");
            var resourceLoader = new AbstractFabricPreparableReloadListener(name, loader);
            ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(resourceLoader);
        });
    }
}
