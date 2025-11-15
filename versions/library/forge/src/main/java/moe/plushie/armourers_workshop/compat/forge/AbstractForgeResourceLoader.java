package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResourceLoader;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Available("[1.16, 1.22)")
public class AbstractForgeResourceLoader implements PreparableReloadListener {

    private final OpenResourceLocation name;
    private final IResourceLoader impl;

    public AbstractForgeResourceLoader(OpenResourceLocation name, IResourceLoader impl) {
        this.name = name;
        this.impl = impl;
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        var resourceManager1 = new AbstractResourceManager(resourceManager);
        var queue = new IResourceLoader.TaskQueue(executor);
        impl.load(resourceManager1, queue);
        return queue.prepare().thenCompose(barrier::wait).thenAcceptAsync(it -> queue.run(), executor2);
    }
}
