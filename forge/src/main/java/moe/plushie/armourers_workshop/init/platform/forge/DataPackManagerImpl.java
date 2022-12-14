package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.CommonNativeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DataPackManagerImpl implements PreparableReloadListener {

    private static boolean isInit = false;
    private static final ResourceLocation identifier = ModConstants.key("data_pack_manager");
    private static final ArrayList<DataPackLoader> loaders = new ArrayList<>();

    public static void register(DataPackLoader loader) {
        loaders.add(loader);
        if (isInit) {
            return;
        }
        isInit = true;
        NotificationCenterImpl.observer(AddReloadListenerEvent .class, event -> event.addListener(new DataPackManagerImpl()));
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier arg, ResourceManager arg2, ProfilerFiller arg3, ProfilerFiller arg4, Executor executor, Executor executor2) {
        ArrayList<Entry> entries = new ArrayList<>();
        IResourceManager resourceManager = CommonNativeManager.createResourceManager(arg2);
        loaders.forEach(loader -> entries.add(new Entry(loader, resourceManager, executor)));
        return CompletableFuture.allOf(entries.stream().map(it -> it.future).toArray(CompletableFuture[]::new)).thenCompose(arg::wait).thenAcceptAsync(void_ -> entries.forEach(Entry::join), executor2);
    }

    @Override
    public String getName() {
        return identifier.toString();
    }

    public static class Entry {

        public DataPackLoader loader;
        public CompletableFuture<Map<ResourceLocation, IDataPackBuilder>> future;

        public Entry(DataPackLoader loader, IResourceManager resourceManager, Executor executor) {
            this.loader = loader;
            this.future = loader.prepare(resourceManager, executor);
        }

        public void join() {
            loader.load(future.join());
        }
    }
}
