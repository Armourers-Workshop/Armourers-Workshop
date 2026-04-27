package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IResourceLoader;
import moe.plushie.armourers_workshop.api.core.IResourceManager;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DataPackLoader implements IResourceLoader {

    private final ArrayList<TaskImpl> allTasks = new ArrayList<>();

    @Override
    public void load(IResourceManager resourceManager, TaskQueue taskQueue) {
        allTasks.forEach(task -> taskQueue.add(resourceManager, task));
    }

    public void addTask(String path, Function<OpenResourceKey, DataPackBuilder> provider, Runnable willLoadHandler, Runnable didLoadHandler, int order) {
        allTasks.add(new TaskImpl(path, provider, willLoadHandler, didLoadHandler, order));
        allTasks.sort(Comparator.naturalOrder());
    }

    public void removeAllTasks() {
        allTasks.clear();
    }

    public boolean isEmpty() {
        return allTasks.isEmpty();
    }

    private static class TaskImpl implements IResourceLoader.Task<Map<OpenResourceKey, DataPackBuilder>>, Comparable<TaskImpl> {

        private final OpenResourceKey target;
        private final Function<OpenResourceKey, DataPackBuilder> provider;
        private final Runnable willLoadHandler;
        private final Runnable didLoadHandler;
        private final int order;

        public TaskImpl(String path, Function<OpenResourceKey, DataPackBuilder> provider, Runnable willLoadHandler, Runnable didLoadHandler, int order) {
            this.target = ModConstants.key(path);
            this.provider = provider;
            this.willLoadHandler = willLoadHandler;
            this.didLoadHandler = didLoadHandler;
            this.order = order;
        }

        @Override
        public Map<OpenResourceKey, DataPackBuilder> prepare(IResourceManager resourceManager) {
            if (willLoadHandler != null) {
                willLoadHandler.run();
            }
            var results = new HashMap<OpenResourceKey, DataPackBuilder>();
            resourceManager.readResources(target, s -> s.endsWith(".json"), (locationIn, resource) -> {
                var key = OpenResourceKey.of(locationIn);
                var object = JsonSerializer.readFromResource(resource);
                if (object == null) {
                    return;
                }
                var path = FileUtils.removeExtension(key.path());
                var key1 = key.withPath(path);
                ModLog.debug("Found a entry '{}' in '{}'", key1, resource.source());
                results.computeIfAbsent(key1, provider).append(object, key);
            });
            return results;
        }

        @Override
        public void apply(Map<OpenResourceKey, DataPackBuilder> results) {
            results.forEach((key, builder) -> builder.build());
            if (didLoadHandler != null) {
                didLoadHandler.run();
            }
        }

        @Override
        public int compareTo(DataPackLoader.TaskImpl o) {
            return order - o.order;
        }
    }
}
