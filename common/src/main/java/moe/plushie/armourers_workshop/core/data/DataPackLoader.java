package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;


public class DataPackLoader {

    private final ResourceLocation target;
    private final Function<ResourceLocation, IDataPackBuilder> provider;
    private final Runnable willLoadHandler;
    private final Runnable didLoadHandler;

    public DataPackLoader(String path, Function<ResourceLocation, IDataPackBuilder> provider, Runnable willLoadHandler, Runnable didLoadHandler) {
        this.target = ModConstants.key(path);
        this.provider = provider;
        this.willLoadHandler = willLoadHandler;
        this.didLoadHandler = didLoadHandler;
    }

    public CompletableFuture<Map<ResourceLocation, IDataPackBuilder>> prepare(IResourceManager resourceManager, Executor executor) {
        if (willLoadHandler != null) {
            willLoadHandler.run();
        }
        return CompletableFuture.supplyAsync(() -> {
            HashMap<ResourceLocation, IDataPackBuilder> results = new HashMap<>();
            resourceManager.readResources(target, s -> s.endsWith(".json"), (location, inputStream) -> {
                IDataPackObject object = StreamUtils.fromPackObject(inputStream);
                if (object == null) {
                    return;
                }
                String path = SkinFileUtils.removeExtension(location.getPath());
                ResourceLocation location1 = new ResourceLocation(location.getNamespace(), path);
                results.computeIfAbsent(location1, provider).append(object, location);
            });
            return results;
        }, executor);
    }

    public void load(Map<ResourceLocation, IDataPackBuilder> results) {
        results.forEach((key, builder) -> builder.build());
        if (didLoadHandler != null) {
            didLoadHandler.run();
        }
    }
}
