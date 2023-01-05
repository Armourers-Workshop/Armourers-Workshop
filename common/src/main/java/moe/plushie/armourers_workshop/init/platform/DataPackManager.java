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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class DataPackManager {

    private static final Gson GSON = new Gson();
//    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1, r -> new Thread(r, "AW-PACK-LD"));

    public static void register(DataPackLoader loader) {
        IResourceManager resourceManager1 = new InJarResourceManager();
        CompletableFuture<Map<ResourceLocation, IDataPackBuilder>> future = loader.prepare(resourceManager1, Runnable::run);
        future.thenAccept(loader::load);
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
