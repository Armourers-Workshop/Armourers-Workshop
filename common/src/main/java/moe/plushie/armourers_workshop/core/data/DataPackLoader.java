package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import manifold.ext.rt.api.auto;

public class DataPackLoader implements PreparableReloadListener {

    protected final ArrayList<Entry> entries = new ArrayList<>();

    public void add(Entry entry) {
        this.entries.add(entry);
        this.entries.sort(Comparator.comparingInt(it -> it.order));
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        ArrayList<CompletableFuture<?>> allJobs = new ArrayList<>();
        ArrayList<Runnable> allCompletes = new ArrayList<>();
        build((supplier, consumer) -> {
            auto job = CompletableFuture.supplyAsync(supplier, executor);
            allJobs.add(job);
            allCompletes.add(() -> consumer.accept(job.join()));
        }, resourceManager);
        return CompletableFuture.allOf(allJobs.toArray(new CompletableFuture[0])).thenCompose(barrier::wait).thenAcceptAsync(it -> allCompletes.forEach(Runnable::run), executor2);
    }

    public void build(TaskQueue taskQueue, ResourceManager resourceManager) {
        IResourceManager resourceManager1 = resourceManager.asResourceManager();
        entries.forEach(entry -> taskQueue.accept(entry.prepare(resourceManager1), entry::load));
    }

    public static class Entry {

        private final ResourceLocation target;
        private final Function<ResourceLocation, IDataPackBuilder> provider;
        private final Runnable willLoadHandler;
        private final Runnable didLoadHandler;
        private final int order;

        public Entry(String path, Function<ResourceLocation, IDataPackBuilder> provider, Runnable willLoadHandler, Runnable didLoadHandler, int order) {
            this.target = ModConstants.key(path);
            this.provider = provider;
            this.willLoadHandler = willLoadHandler;
            this.didLoadHandler = didLoadHandler;
            this.order = order;
        }

        public Supplier<Map<ResourceLocation, IDataPackBuilder>> prepare(IResourceManager resourceManager) {
            if (willLoadHandler != null) {
                willLoadHandler.run();
            }
            return () -> {
                HashMap<ResourceLocation, IDataPackBuilder> results = new HashMap<>();
                resourceManager.readResources(target, s -> s.endsWith(".json"), (location, resource) -> {
                    IDataPackObject object = StreamUtils.fromPackObject(resource);
                    if (object == null) {
                        return;
                    }
                    String path = SkinFileUtils.removeExtension(location.getPath());
                    ResourceLocation location1 = new ResourceLocation(location.getNamespace(), path);
                    ModLog.debug("Load entry '{}' in '{}'", location1, resource.getSource());
                    results.computeIfAbsent(location1, provider).append(object, location);
                });
                return results;
            };
        }

        public void load(Map<ResourceLocation, IDataPackBuilder> results) {
            results.forEach((key, builder) -> builder.build());
            if (didLoadHandler != null) {
                didLoadHandler.run();
            }
        }
    }

    protected interface TaskQueue {

        void accept(Supplier<Map<ResourceLocation, IDataPackBuilder>> supplier, Consumer<Map<ResourceLocation, IDataPackBuilder>> consumer);
    }
}
