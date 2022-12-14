package moe.plushie.armourers_workshop.core.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;


public class DataPackLoader {

    private static final Gson GSON = new Gson();

    private final String path;
    private final Function<ResourceLocation, IDataPackBuilder> provider;
    private final Runnable willLoadHandler;
    private final Runnable didLoadhandler;

    public DataPackLoader(String path, Function<ResourceLocation, IDataPackBuilder> provider, Runnable willLoadHandler, Runnable didLoadhandler) {
        this.path = path;
        this.provider = provider;
        this.willLoadHandler = willLoadHandler;
        this.didLoadhandler = didLoadhandler;
    }

    public CompletableFuture<Map<ResourceLocation, IDataPackBuilder>> prepare(IResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<ResourceLocation, IDataPackBuilder> results = new HashMap<>();
            resourceManager.readResources(path, s -> s.endsWith(".json"), (location, inputStream) -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                JsonObject object = StreamUtils.fromJson(GSON, reader, JsonObject.class);
                if (object == null) {
                    return;
                }
                String path = SkinFileUtils.removeExtension(location.getPath());
                ResourceLocation location1 = new ResourceLocation(location.getNamespace(), path);
                results.computeIfAbsent(location1, provider).append(IDataPackObject.of(object), location);
            });
            return results;
        }, executor);
    }

    public void load(Map<ResourceLocation, IDataPackBuilder> results) {
        willLoadHandler.run();
        results.forEach((key, builder) -> builder.build());
        didLoadhandler.run();
    }
}
