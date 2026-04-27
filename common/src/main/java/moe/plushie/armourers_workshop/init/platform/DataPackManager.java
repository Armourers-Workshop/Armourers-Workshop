package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.core.IResourceManager;
import moe.plushie.armourers_workshop.compat.core.AbstractBundleResourceManager;
import moe.plushie.armourers_workshop.core.data.DataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;

import java.util.Map;
import java.util.function.Function;

public class DataPackManager {

    private static final Map<DataPackType, DataPackLoader> INSTANCES = Collections.immutableMap(it -> {
        it.put(DataPackType.SERVER_DATA, new Data());
        it.put(DataPackType.CLIENT_RESOURCES, new Resources());
        it.put(DataPackType.BUNDLED_DATA, new Bundle());
    });

    public static DataPackLoader byType(DataPackType packType) {
        return INSTANCES.get(packType);
    }

    public static void register(DataPackType packType, String path, Function<OpenResourceKey, DataPackBuilder> provider, Runnable willLoadHandler, Runnable didLoadHandler, int order) {
        var loader = byType(packType);
        if (loader != null) {
            loader.addTask(path, provider, willLoadHandler, didLoadHandler, order);
        }
    }

    protected static class Bundle extends DataPackLoader {

        @Override
        public void load(IResourceManager resourceManager, TaskQueue taskQueue) {
            super.load(resourceManager, taskQueue);
            removeAllTasks();
            EventManager.post(DataPackEvent.Reloading.class, () -> DataPackType.BUNDLED_DATA);
        }
    }

    protected static class Data extends DataPackLoader {

        @Override
        public void load(IResourceManager resourceManager, TaskQueue taskQueue) {
            var loader = byType(DataPackType.BUNDLED_DATA);
            if (loader != null && !loader.isEmpty()) {
                loader.load(new AbstractBundleResourceManager(resourceManager), taskQueue);
            }
            super.load(resourceManager, taskQueue);
            EventManager.post(DataPackEvent.Reloading.class, () -> DataPackType.SERVER_DATA);
        }
    }

    protected static class Resources extends DataPackLoader {

        @Override
        public void load(IResourceManager resourceManager, TaskQueue taskQueue) {
            var loader = byType(DataPackType.BUNDLED_DATA);
            if (loader != null && !loader.isEmpty()) {
                loader.load(new AbstractBundleResourceManager(resourceManager), taskQueue);
            }
            super.load(resourceManager, taskQueue);
            EventManager.post(DataPackEvent.Reloading.class, () -> DataPackType.CLIENT_RESOURCES);
        }
    }
}
