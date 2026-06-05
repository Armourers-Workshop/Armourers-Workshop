package moe.plushie.armourers_workshop.compat.client.entity.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.client.render.model.CachedModel;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.NamedClass;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractModelHolder {

    private static final HashMap<Class<?>, Entry> ENTITIES = new HashMap<>();

    public static <S> IEntityModel<S> create(IEntityModel<S> entityModel) {
        return new LazyModel<>(model -> {
            var exists = new HashSet<>();
            var builders = new ArrayList<Map<String, String>>();
            var clazz = (Class<?>) entityModel.getClass();
            while (clazz != Object.class) {
                var entry = findEntry(clazz);
                if (entry != null && exists.add(entry)) {
                    builders.add(entry.mapper);
                }
                clazz = clazz.getSuperclass();
            }
            var allParts = parseModel(entityModel);
            builders.forEach(mapper -> mapper.forEach((target, source) -> {
                var part = allParts.remove(source);
                if (part != null) {
                    model.put(target, new AbstractModelPartHolder(target, part));
                }
            }));
            allParts.forEach((key, part) -> {
                if (!key.contains(".")) {
                    model.unnamed(new AbstractModelPartHolder(null, part));
                }
            });
            // the baby head is a special part.
            model.put("baby", new AbstractBabyModelHolder(entityModel));
        });
    }

    public static void register(Class<?> clazz, Map<String, String> mapper) {
        ENTITIES.put(clazz, new Entry(clazz, mapper));
    }

    private static Map<String, ModelPart> parseModel(IEntityModel<?> model) {
        var builder = new AbstractModelCollector.Builder();
        if (model instanceof AbstractModelCollector collector) {
            collector.aw2$collect(builder);
        }
        return builder.build();
    }

    private static Entry findEntry(Class<?> clazz) {
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

    private static class Entry {

        private static final Map<Class<?>, Map<String, String>> FIXER = Collections.immutableMap(builder -> {
            var values = new HashMap<String, Map<String, String>>();
            AbstractModelCollectorImpl.apply(values);
            values.forEach((key, value) -> {
                var clazz = NamedClass.forName(key);
                if (clazz != null) {
                    builder.put(clazz, value);
                }
            });
        });

        private final Class<?> clazz;
        private final Map<String, String> mapper;

        public Entry(Class<?> clazz, Map<String, String> mapper) {
            this.clazz = clazz;
            this.mapper = apply(clazz, mapper);
        }

        private Map<String, String> apply(Class<?> clazz, Map<String, String> mapper) {
            // fast fix the name.
            var fixer = FIXER.get(clazz);
            if (fixer == null || fixer.isEmpty()) {
                return mapper;
            }
            var fixedMapper = new LinkedHashMap<String, String>();
            mapper.forEach((key, value) -> fixedMapper.put(key, fixer.getOrDefault(value, value)));
            return fixedMapper;
        }
    }

    private static class LazyModel<S> extends CachedModel<S> {

        private Consumer<LazyModel<S>> provider;

        public LazyModel(Consumer<LazyModel<S>> provider) {
            this.provider = provider;
        }

        @Override
        public IModelPart abi$getPartByName(String name) {
            if (provider != null) {
                provider.accept(this);
                provider = null;
            }
            return super.abi$getPartByName(name);
        }

        @Override
        public Collection<? extends IModelPart> abi$allParts() {
            if (provider != null) {
                provider.accept(this);
                provider = null;
            }
            return super.abi$allParts();
        }
    }
}
