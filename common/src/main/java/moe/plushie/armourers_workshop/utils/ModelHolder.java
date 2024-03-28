package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.core.client.model.CachedModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ModelHolder {

    private static final HashMap<Class<?>, Entry<?, ?>> ENTRIES = new HashMap<>();

    public static <V extends Model, M extends IModel> M ofNullable(V model) {
        if (model != null) {
            return of(model);
        }
        return null;
    }

    public static <M extends IModel> M of(Model model) {
        IAssociatedObjectProvider provider = ObjectUtils.safeCast(model, IAssociatedObjectProvider.class);
        if (provider != null) {
            M holder = provider.getAssociatedObject();
            if (holder != null) {
                return holder;
            }
            holder = createHolder(model);
            provider.setAssociatedObject(holder);
            return holder;
        }
        // If the model can't support the cache, we create it every time,
        // although it causes performance degradation, but it works fine.
        return createHolder(model);
    }

    public static <T extends Model> void register(Class<T> clazz, BiConsumer<T, Container> builder) {
        ENTRIES.put(clazz, new Entry<>(clazz, null, builder));
    }

    public static <T extends Model> void registerOptional(Class<T> clazz, BiConsumer<T, Container> builder) {
        if (clazz != null) {
            register(clazz, builder);
        }
    }

    private static <V extends Model, M extends IModel> M createHolder(V model) {
        HashSet<Entry<?, ?>> exists = new HashSet<>();
        ArrayList<BiConsumer<V, Container>> builders = new ArrayList<>();
        Function<Container, M> factory = null;
        Class<?> clazz = model.getClass();
        while (clazz != Object.class) {
            Entry<V, M> entry = getEntry(clazz);
            if (entry != null && exists.add(entry)) {
                builders.add(entry.builder);
                if (factory == null) {
                    factory = entry.factory;
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (factory == null) {
            Function<Container, CachedModel<ModelPart>> factory1 = CachedModel::new;
            factory = ObjectUtils.unsafeCast(factory1);
        }
        Container container = new Container(model);
        builders.forEach(builder -> builder.accept(model, container));
        return factory.apply(container);
    }

    private static <V extends Model, M extends IModel> Entry<V, M> getEntry(Class<?> clazz) {
        Entry<?, ?> entry = ENTRIES.get(clazz);
        if (entry == null) {
            // this is root class?
            if (clazz == Object.class) {
                return null;
            }
            // autofill by parent entry.
            entry = getEntry(clazz.getSuperclass());
            ENTRIES.put(clazz, entry);
        }
        return ObjectUtils.unsafeCast(entry);
    }

    public static class Entry<T extends Model, M extends IModel> {

        Class<T> clazz;
        Function<Container, M> factory;
        BiConsumer<T, Container> builder;

        Entry(Class<T> clazz, Function<Container, M> factory, BiConsumer<T, Container> builder) {
            this.clazz = clazz;
            this.factory = factory;
            this.builder = builder;
        }
    }

    public static class Container extends CachedModel.Container<ModelPart> {

        private final EntityModel<?> model;

        public Container(Model model) {
            super(model.getClass(), ModelPartHolder::of);
            this.model = ObjectUtils.safeCast(model, EntityModel.class);
            this.babyPose = model.getBabyPose();
        }

        @Override
        public IModelBabyPose getBabyPose() {
            if (model != null && model.young) {
                return super.getBabyPose();
            }
            return null;
        }
    }
}
