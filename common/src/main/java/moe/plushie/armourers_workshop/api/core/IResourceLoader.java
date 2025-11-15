package moe.plushie.armourers_workshop.api.core;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface IResourceLoader {

    void load(IResourceManager resourceManager, TaskQueue queue);

    interface Task<T> {

        T prepare(IResourceManager resourceManager);

        void apply(T results);
    }

    class TaskQueue {

        private final Executor executor;

        private final ArrayList<CompletableFuture<?>> prepareActions = new ArrayList<>();
        private final ArrayList<Runnable> applyActions = new ArrayList<>();

        public TaskQueue(Executor executor) {
            this.executor = executor;
        }

        public <T> void add(IResourceManager resourceManager, IResourceLoader.Task<T> task) {
            var job = CompletableFuture.supplyAsync(() -> task.prepare(resourceManager), executor);
            prepareActions.add(job);
            applyActions.add(() -> task.apply(job.join()));
        }

        public CompletableFuture<Void> prepare() {
            return CompletableFuture.allOf(prepareActions.toArray(new CompletableFuture[0]));
        }

        public void run() {
            applyActions.forEach(Runnable::run);
        }

        public Executor executor() {
            return executor;
        }
    }
}
