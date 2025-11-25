package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.client.render.model.CachedModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class EpicFlightModelHolder {

    private static final HashMap<Class<?>, Entry<?>> ENTITIES = new HashMap<>();

    public static <S extends EntityRenderState> IEntityModel<S> create(Object entityModel) {
        var model = new CachedModel<S>();
        var exists = new HashSet<>();
        var builders = new ArrayList<Entry<?>>();
        var clazz = (Class<?>) entityModel.getClass();
        while (clazz != Object.class) {
            var entry = findEntry(clazz);
            if (entry != null && exists.add(entry)) {
                builders.add(entry);
            }
            clazz = clazz.getSuperclass();
        }
        builders.forEach(it -> it.apply(entityModel, model));
        return model;
    }

    public static <M> void register(Class<M> clazz, BiConsumer<M, Map<String, EpicFlightModelPart>> builder) {
        ENTITIES.put(clazz, new Entry<>(clazz, builder));
    }

    private static Entry<?> findEntry(Class<?> clazz) {
        var entry = ENTITIES.get(clazz);
        if (entry == null) {
            // this is root class?
            if (clazz == Object.class) {
                return null;
            }
            // autofill by parent entry.
            entry = findEntry(clazz.getSuperclass());
            ENTITIES.put(clazz, entry);
        }
        return entry;
    }

    private static class Entry<M> {

        private final Class<M> clazz;
        private final BiConsumer<M, Map<String, EpicFlightModelPart>> builder;

        public Entry(Class<M> clazz, BiConsumer<M, Map<String, EpicFlightModelPart>> builder) {
            this.clazz = clazz;
            this.builder = builder;
        }

        public void apply(Object source, CachedModel<?> model) {
            var parts = new LinkedHashMap<String, EpicFlightModelPart>();
            builder.accept(clazz.cast(source), parts);
            parts.forEach((key, value) -> model.put(key, new IModelPart() {
                @Override
                public boolean isVisible() {
                    return true;
                }

                @Override
                public void setVisible(boolean visible) {
                    if (value != null) {
                        value.setVisible(visible);
                    }
                }

                @Override
                public IModelPartPose pose() {
                    return null;
                }
            }));
        }
    }
}
