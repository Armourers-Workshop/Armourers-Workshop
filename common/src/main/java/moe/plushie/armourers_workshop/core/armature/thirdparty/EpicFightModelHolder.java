package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.client.model.CachedModel;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EpicFightModelHolder {

    private static final HashMap<Class<?>, Entry<?, ?, ?>> ENTRIES = new HashMap<>();
    private static final HashMap<Object, IModel> MODELS = new HashMap<>();

    public static <T> IModel of(T model) {
        IModel value = MODELS.get(model);
        if (value != null) {
            return value;
        }
        value = createHolder(model);
        MODELS.put(model, value);
        return createHolder(model);
    }

    public static <M, P> void register(Class<M> clazz, Function<CachedModel.Container<P>, IModel> factory, Function<P, IModelPart> transformer, BiConsumer<M, CachedModel.Container<P>> builder) {
        ENTRIES.put(clazz, new Entry<>(clazz, factory, transformer, builder));
    }

    private static <T, P, M extends IModel> M createHolder(T model) {
        ArrayList<BiConsumer<T, CachedModel.Container<P>>> builders = new ArrayList<>();
        Function<CachedModel.Container<P>, M> factory = null;
        Function<P, IModelPart> transformer = null;
        Class<?> clazz = model.getClass();
        while (clazz != Object.class) {
            Entry<?, ?, ?> entry = ENTRIES.get(clazz);
            if (entry != null) {
                Entry<T, P, M> entry1 = ObjectUtils.unsafeCast(entry);
                builders.add(entry1.builder);
                if (factory == null) {
                    factory = entry1.factory;
                }
                if (transformer == null) {
                    transformer = entry1.transformer;
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (factory == null) {
            Function<CachedModel.Container<P>, CachedModel<P>> factory1 = CachedModel::new;
            factory = ObjectUtils.unsafeCast(factory1);
        }
        CachedModel.Container<P> container = new CachedModel.Container<>(transformer);
        builders.forEach(builder -> builder.accept(model, container));
        return factory.apply(container);
    }

    public static class Entry<T, P, M extends IModel> {

        Class<T> clazz;
        Function<P, IModelPart> transformer;
        Function<CachedModel.Container<P>, M> factory;
        BiConsumer<T, CachedModel.Container<P>> builder;

        Entry(Class<T> clazz, Function<CachedModel.Container<P>, M> factory, Function<P, IModelPart> transformer, BiConsumer<T, CachedModel.Container<P>> builder) {
            this.clazz = clazz;
            this.factory = factory;
            this.builder = builder;
            this.transformer = transformer;
        }
    }
}

