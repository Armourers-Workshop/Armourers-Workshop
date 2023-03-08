package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class DataPackManager {

    private static final InDataResourceManager IN_DATA_RESOURCE_MANAGER = new InDataResourceManager();
    private static final InJarResourceManager IN_JAR_RESOURCE_MANAGER = new InJarResourceManager();

    public static void register(DataPackType packType, DataPackLoader loader) {
        switch (packType) {
            case JAR: {
                CompletableFuture<Map<ResourceLocation, IDataPackBuilder>> future = loader.prepare(IN_JAR_RESOURCE_MANAGER, Runnable::run);
                future.thenAccept(loader::load);
                break;
            }
            case DATA: {
                IN_DATA_RESOURCE_MANAGER.loaders.add(loader);
                break;
            }
            case ASSET: {
                // no impl yet.
                break;
            }
        }
    }

    public static InDataResourceManager getLoader() {
        return IN_DATA_RESOURCE_MANAGER;
    }

    public static class InDataResourceManager implements PreparableReloadListener {

        private final ArrayList<DataPackLoader> loaders = new ArrayList<>();

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
            ArrayList<Runnable> tasks = new ArrayList<>();
            ArrayList<CompletableFuture<?>> entries = new ArrayList<>();
            IResourceManager resourceManager1 = CommonNativeManager.createResourceManager(resourceManager);
            loaders.forEach(loader -> {
                CompletableFuture<Map<ResourceLocation, IDataPackBuilder>> future = loader.prepare(resourceManager1, executor);
                entries.add(future);
                tasks.add(() -> loader.load(future.join()));
            });
            return CompletableFuture.allOf(entries.toArray(new CompletableFuture[0])).thenCompose(barrier::wait).thenAcceptAsync(void_ -> tasks.forEach(Runnable::run), executor2);
        }
    }

    public static class InJarResourceManager implements IResourceManager {

        private List<String> files;
        private final ClassLoader classLoader;

        public InJarResourceManager() {
            this.classLoader = InJarResourceManager.class.getClassLoader();
        }

        @Override
        public boolean hasResource(ResourceLocation resourceLocation) {
            return getFiles().contains(resolve(resourceLocation));
        }

        @Override
        public InputStream readResource(ResourceLocation resourceLocation) throws IOException {
            return classLoader.getResourceAsStream(resolve(resourceLocation));
        }

        @Override
        public void readResources(ResourceLocation target, Predicate<String> validator, BiConsumer<ResourceLocation, InputStream> consumer) {
            String base = resolve(target.getNamespace(), "");
            getFiles().forEach(it -> {
                if (!it.startsWith(base)) {
                    return;
                }
                String name = SkinFileUtils.getRelativePath(it, base);
                if (!name.startsWith(target.getPath()) || !validator.test(name)) {
                    return;
                }
                InputStream stream = classLoader.getResourceAsStream(it);
                if (stream != null) {
                    consumer.accept(ModConstants.key(name), stream);
                }
            });
        }

        private String resolve(ResourceLocation location) {
            return resolve(location.getNamespace(), location.getPath());
        }

        private String resolve(String namespace, String path) {
            return  "data/" + namespace + "/" + path;
        }

        private Collection<String> getFiles() {
            if (files != null) {
                return files;
            }
            ArrayList<String> results = new ArrayList<>();
            InputStream inputStream = classLoader.getResourceAsStream("pack.dat");
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                try {
                    while (true) {
                        String file = reader.readLine();
                        if (file == null) {
                            break;
                        }
                        results.add(file);
                    }
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            files = results;
            return results;
        }
    }
}
