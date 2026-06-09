package moe.plushie.armourers_workshop.compat.fabric.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Available("[16, 26)")
public class AbstractFabricPreparableReloadListener implements IdentifiableResourceReloadListener {

    private final OpenResourceKey name;
    private final OpenResourceManager.PreparableReloadListener reloadListener;

    public AbstractFabricPreparableReloadListener(OpenResourceKey name, OpenResourceManager.PreparableReloadListener reloadListener) {
        this.name = name;
        this.reloadListener = reloadListener;
    }

    @Override
    public ResourceLocation getFabricId() {
        return name.get();
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        return reloadListener.reload(AbstractResourceManager.wrap(resourceManager), executor, barrier::wait, executor2);
    }
}
