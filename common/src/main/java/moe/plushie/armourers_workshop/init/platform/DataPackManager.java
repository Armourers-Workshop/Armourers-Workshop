package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.compat.core.AbstractBundleResourceManager;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;
import moe.plushie.armourers_workshop.init.event.client.RegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import moe.plushie.armourers_workshop.init.event.common.RegisterServerDataPackEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataPackManager {

    private static final ServerData SERVER_DATA = new ServerData();
    private static final ClientResources CLIENT_RESOURCES = new ClientResources();
    private static final BundleData BUNDLED_DATA = new BundleData();

    public static void init() {
        EventBus.register(RegisterClientDataPackEvent.class, event -> event.register(CLIENT_RESOURCES));
        EventBus.register(RegisterServerDataPackEvent.class, event -> event.register(SERVER_DATA));
    }

    public static void register(DataPackType type, Supplier<DataPackLoader> loader) {
        switch (type) {
            case SERVER_DATA -> SERVER_DATA.add(loader.get());
            case CLIENT_RESOURCES -> CLIENT_RESOURCES.add(loader.get());
            case BUNDLED_DATA -> BUNDLED_DATA.add(loader.get());
        }
    }

    public static void addReloadListener(DataPackType type, Consumer<OpenResourceManager> reloadListener) {
        EventBus.register(DataPackEvent.Reloading.class, event -> {
            if (event.type() == type) {
                reloadListener.accept(event.resourceManager());
            }
        });
    }

    private static abstract class Dispatcher implements OpenResourceManager.PreparableReloadListener {

        protected final List<DataPackLoader> loaders = new ArrayList<>();

        @Override
        public CompletableFuture<Void> reload(OpenResourceManager resourceManager, Executor taskExecutor, OpenResourceManager.PreparableReloadListener.PreparationBarrier preparationBarrier, Executor reloadExecutor) {
            // prepare the task.
            var tasks = new TaskQueue();
            prepare(resourceManager, tasks);
            return tasks.build(taskExecutor, preparationBarrier, reloadExecutor);
        }

        protected abstract void prepare(OpenResourceManager resourceManager, TaskQueue taskQueue);

        public void add(DataPackLoader loader) {
            loaders.add(loader);
        }

        public void clear() {
            loaders.clear();
        }

        public boolean isEmpty() {
            return loaders.isEmpty();
        }

        public static class TaskQueue {

            private final List<Runnable> beginActions = new ArrayList<>();
            private final List<Runnable> loadActions = new ArrayList<>();
            private final List<Runnable> endActions = new ArrayList<>();

            public void add(List<DataPackLoader> loaders, OpenResourceManager resourceManager) {
                var tasks = new ArrayList<>(loaders);
                tasks.sort(Comparator.comparing(DataPackLoader::priority));
                tasks.forEach((task) -> {
                    beginActions.add(() -> task.begin(resourceManager));
                    loadActions.add(() -> task.load(resourceManager));
                    endActions.add(() -> task.end(resourceManager));
                });
            }

            public void emit(DataPackType packType, OpenResourceManager resourceManager) {
                endActions.add(() -> EventBus.post(DataPackEvent.Reloading.class, new DataPackEvent.Reloading() {
                    @Override
                    public DataPackType type() {
                        return packType;
                    }

                    @Override
                    public OpenResourceManager resourceManager() {
                        return resourceManager;
                    }
                }));
            }

            public CompletableFuture<Void> build(Executor taskExecutor, OpenResourceManager.PreparableReloadListener.PreparationBarrier preparationBarrier, Executor reloadExecutor) {
                return CompletableFuture
                        .runAsync(() -> beginActions.forEach(Runnable::run), taskExecutor)
                        .thenRunAsync(() -> loadActions.forEach(Runnable::run), taskExecutor)
                        .thenCompose(preparationBarrier::wait)
                        .thenRunAsync(() -> endActions.forEach(Runnable::run), reloadExecutor);
            }
        }
    }

    private static class BundleData extends Dispatcher {

        @Override
        protected void prepare(OpenResourceManager resourceManager, TaskQueue taskQueue) {
            taskQueue.add(loaders, resourceManager);
            taskQueue.emit(DataPackType.BUNDLED_DATA, resourceManager);
            clear();
        }
    }

    private static class ServerData extends Dispatcher {

        @Override
        protected void prepare(OpenResourceManager resourceManager, TaskQueue taskQueue) {
            // only call the bundle resource task once.
            if (!BUNDLED_DATA.isEmpty()) {
                BUNDLED_DATA.prepare(AbstractBundleResourceManager.wrap(resourceManager), taskQueue);
            }
            taskQueue.add(loaders, resourceManager);
            taskQueue.emit(DataPackType.SERVER_DATA, resourceManager);
        }
    }

    private static class ClientResources extends Dispatcher {

        @Override
        protected void prepare(OpenResourceManager resourceManager, TaskQueue taskQueue) {
            // only call the bundle resource task once.
            if (!BUNDLED_DATA.isEmpty()) {
                BUNDLED_DATA.prepare(AbstractBundleResourceManager.wrap(resourceManager), taskQueue);
            }
            taskQueue.add(loaders, resourceManager);
            taskQueue.emit(DataPackType.CLIENT_RESOURCES, resourceManager);
        }
    }
}
