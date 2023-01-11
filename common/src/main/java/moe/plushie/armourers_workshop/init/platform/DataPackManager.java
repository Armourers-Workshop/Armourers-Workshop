package moe.plushie.armourers_workshop.init.platform;

import com.google.gson.Gson;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.resources.ResourceLocation;

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

        private List<String> files;
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
            String base = "data/" + ModConstants.MOD_ID + "/";
            getFiles().forEach(it -> {
                if (!it.startsWith(base)) {
                    return;
                }
                String name = it.replace(base, "");
                if (!name.startsWith(path) || !validator.test(name)) {
                    return;
                }
                InputStream stream = classLoader.getResourceAsStream(it);
                if (stream != null) {
                    consumer.accept(ModConstants.key(name), stream);
                }
            });
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
                    while (true){
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
