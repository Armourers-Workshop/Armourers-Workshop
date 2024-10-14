package moe.plushie.armourers_workshop.init.platform;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.init.platform.event.common.DataPackEvent;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.function.Function;

public class DataPackManager {

    private static final ImmutableMap<DataPackType, DataPackLoader> INSTANCES = ImmutableMap.<DataPackType, DataPackLoader>builder()
            .put(DataPackType.SERVER_DATA, new Data())
            .put(DataPackType.CLIENT_RESOURCES, new Resources())
            .put(DataPackType.BUNDLED_DATA, new Bundle())
            .build();

    public static DataPackLoader byType(DataPackType packType) {
        return INSTANCES.get(packType);
    }

    public static void register(DataPackType packType, String path, Function<IResourceLocation, IDataPackBuilder> provider, Runnable willLoadHandler, Runnable didLoadHandler, int order) {
        var loader = byType(packType);
        if (loader != null) {
            loader.add(new DataPackLoader.Entry(path, provider, willLoadHandler, didLoadHandler, order));
        }
    }

    protected static class Bundle extends DataPackLoader {

        @Override
        public void build(TaskQueue taskQueue, ResourceManager resourceManager) {
            super.build(taskQueue, resourceManager);
            entries.clear();
            EventManager.post(DataPackEvent.Reloading.class, () -> DataPackType.BUNDLED_DATA);
        }
    }

    protected static class Data extends DataPackLoader {

        @Override
        public void build(TaskQueue taskQueue, ResourceManager resourceManager) {
            var loader = byType(DataPackType.BUNDLED_DATA);
            if (loader != null && !loader.isEmpty()) {
                loader.build(taskQueue, resourceManager.asBundleManager());
            }
            super.build(taskQueue, resourceManager);
            EventManager.post(DataPackEvent.Reloading.class, () -> DataPackType.SERVER_DATA);
        }
    }

    protected static class Resources extends DataPackLoader {

        @Override
        public void build(TaskQueue taskQueue, ResourceManager resourceManager) {
            var loader = byType(DataPackType.BUNDLED_DATA);
            if (loader != null && !loader.isEmpty()) {
                loader.build(taskQueue, resourceManager.asBundleManager());
            }
            super.build(taskQueue, resourceManager);
            EventManager.post(DataPackEvent.Reloading.class, () -> DataPackType.CLIENT_RESOURCES);
        }
    }
}
