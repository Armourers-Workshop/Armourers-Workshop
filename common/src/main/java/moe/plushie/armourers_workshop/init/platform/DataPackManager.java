package moe.plushie.armourers_workshop.init.platform;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DataPackManager {

    private static final Gson GSON = new Gson();
    private static final ArrayList<DataPackLoader> LOADERS = new ArrayList<>();

    public static void register(DataPackLoader loader) {
        LOADERS.add(loader);
    }

    public static void init(Consumer<PreparableReloadListener> registry) {
        registry.accept(((barrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2) -> {
            ArrayList<Runnable> tasks = new ArrayList<>();
            ArrayList<CompletableFuture<?>> entries = new ArrayList<>();
            IResourceManager resourceManager1 = new InJarResourceManager();
            LOADERS.forEach(loader -> {
                CompletableFuture<Map<ResourceLocation, IDataPackBuilder>> future = loader.prepare(resourceManager1, executor);
                entries.add(future);
                tasks.add(() -> loader.load(future.join()));
            });
            return CompletableFuture.allOf(entries.toArray(new CompletableFuture[0])).thenCompose(barrier::wait).thenAcceptAsync(void_ -> tasks.forEach(Runnable::run), executor2);
        }));
    }

    public static class InJarResourceManager implements IResourceManager {

        private IDataPackObject packObject;
        private final ClassLoader classLoader;

        public InJarResourceManager() {
            this.classLoader = InJarResourceManager.class.getClassLoader();
        }

        @Override
        public boolean hasResource(ResourceLocation resourceLocation) {
            return false;
        }

        @Override
        public InputStream readResource(ResourceLocation resourceLocation) throws IOException {
            return null;
        }

        @Override
        public void readResources(String path, Predicate<String> validator, BiConsumer<ResourceLocation, InputStream> consumer) {
            IDataPackObject object = getPackInfo();
            object.get("providers").allValues().forEach(it -> {
                String name = it.stringValue();
                if (!name.startsWith(path) || !validator.test(name)) {
                    return;
                }
                InputStream stream = classLoader.getResourceAsStream("data/" + ModConstants.MOD_ID + "/" + name);
                if (stream != null) {
                    consumer.accept(ModConstants.key(name), stream);
                }
            });
        }

        private IDataPackObject getPackInfo() {
            if (packObject != null) {
                return packObject;
            }
            InputStream inputStream = classLoader.getResourceAsStream("armourersworkshop-datapacks.json");
            if (inputStream == null) {
                packObject = () -> null;
                return packObject;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            JsonObject object = StreamUtils.fromJson(GSON, reader, JsonObject.class);
            packObject = () -> object;
            return packObject;
        }
    }
}
