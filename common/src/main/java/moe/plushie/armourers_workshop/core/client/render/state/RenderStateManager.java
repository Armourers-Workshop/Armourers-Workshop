package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RenderStateManager<T, S extends RenderState> {

    private static final Map<Class<?>, Entry<?, ?>> ENTITIES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, RenderStateManager<?, ?>> EXTRACTORS = new ConcurrentHashMap<>();

    private final ArrayList<Entry<?, ?>> entries;

    private RenderStateManager(ArrayList<Entry<?, ?>> entries) {
        this.entries = entries;
    }

    public S createRenderState(T entity) {
        if (entries.isEmpty()) {
            return null;
        }
        // noinspection unchecked
        var entry = (Entry<T, S>) entries.get(0);
        return entry.stateFactory.get();
    }

    public void extractRenderState(T entity, S renderState) {
        var count = entries.size();
        for (int i = count; i > 0; i--) {
            // noinspection unchecked
            var entry = (Entry<T, S>) entries.get(i - 1);
            entry.extractHandler.accept(entity, renderState);
        }
    }

    public static <T, S extends RenderState> RenderStateManager<T, S> create(T entity) {
        return Objects.unsafeCast(EXTRACTORS.computeIfAbsent(entity.getClass(), clazz -> {
            var selectedEntries = new ArrayList<Entry<?, ?>>();
            for (var selectedClass : getHierarchy(clazz)) {
                var entry = ENTITIES.get(selectedClass);
                if (entry != null) {
                    selectedEntries.add(entry);
                }
            }
            return new RenderStateManager<>(selectedEntries);
        }));
    }

    public static <T, S extends RenderState> void register(Class<T> entityClass, Supplier<S> stateFactory, BiConsumer<T, S> extractHandler) {
        var entry = new Entry<>(entityClass, stateFactory, extractHandler);
        ENTITIES.put(entityClass, entry);
    }

    private static ArrayList<Class<?>> getHierarchy(Class<?> clazz) {
        var inherited = new ArrayList<Class<?>>();
        var interfaces = new ArrayList<Class<?>>();
        while (clazz != null && clazz != Object.class) {
            // search all interfaces.
            var pending = Collections.newList(clazz.getInterfaces());
            for (var i = 0; i < pending.size(); ++i) {
                var it = pending.get(i);
                interfaces.remove(it);
                interfaces.add(it);
                pending.addAll(i + 1, Collections.newList(it.getInterfaces()));
            }
            inherited.add(clazz);
            clazz = clazz.getSuperclass();
        }
        inherited.addAll(interfaces);
        return inherited;
    }

    private static class Entry<T, S> {

        private final Class<T> entityClass;
        private final Supplier<S> stateFactory;
        private final BiConsumer<T, S> extractHandler;

        private Entry(Class<T> entityClass, Supplier<S> stateFactory, BiConsumer<T, S> extractHandler) {
            this.entityClass = entityClass;
            this.stateFactory = stateFactory;
            this.extractHandler = extractHandler;
        }
    }
}
