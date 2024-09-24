package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterDataPackEvent;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Available("[1.16, )")
public class AbstractFabricRegisterDataPackEvent {

    public static IEventHandler<RegisterDataPackEvent> registryFactory() {
        return (priority, receiveCancelled, subscriber) -> subscriber.accept(provider -> {
            var registryName = ModConstants.key("custom-data-pack").toLocation();
            ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return registryName;
                }

                @Override
                public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                    return provider.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
                }
            });
        });
    }
}
