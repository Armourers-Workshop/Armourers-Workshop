package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.client.model.CachedModel;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class EpicFlightModelTransformer<T, P, M extends IModel> {

    private static final HashMap<Class<?>, EpicFlightModelTransformer<?, ?, ?>> ENTRIES = new HashMap<>();

    Class<T> clazz;
    Function<P, IModelPart> transformer;
    Function<CachedModel.Container<P>, M> factory;
    BiConsumer<T, CachedModel.Container<P>> builder;


    public static <M extends EpicFlightModelPartBuilder, P extends Consumer<Boolean>> void register(Class<M> clazz, Function<CachedModel.Container<P>, IModel> factory, BiConsumer<M, CachedModel.Container<P>> builder) {
        var entry = new EpicFlightModelTransformer<M, P, IModel>();
        entry.clazz = clazz;
        entry.factory = factory;
        entry.builder = builder;
        entry.transformer = modelPart -> new IModelPart() {
            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public void setVisible(boolean visible) {
                if (modelPart != null) {
                    modelPart.accept(visible);
                }
            }

            @Override
            public IModelPartPose pose() {
                return null;
            }
        };
        ENTRIES.put(clazz, entry);
    }

    public static <M, P> void register(Class<M> clazz, Function<CachedModel.Container<P>, IModel> factory, Function<P, IModelPart> transformer, BiConsumer<M, CachedModel.Container<P>> builder) {
        var entry = new EpicFlightModelTransformer<M, P, IModel>();
        entry.clazz = clazz;
        entry.factory = factory;
        entry.builder = builder;
        entry.transformer = transformer;
        ENTRIES.put(clazz, entry);
    }

    public static <T, P, M extends IModel> M create(T model) {
        var builders = new ArrayList<BiConsumer<T, CachedModel.Container<P>>>();
        Function<CachedModel.Container<P>, M> factory = null;
        Function<P, IModelPart> transformer = null;
        Class<?> clazz = model.getClass();
        while (clazz != Object.class) {
            EpicFlightModelTransformer<?, ?, ?> entry = ENTRIES.get(clazz);
            if (entry != null) {
                EpicFlightModelTransformer<T, P, M> entry1 = ObjectUtils.unsafeCast(entry);
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
        CachedModel.Container<P> container = new CachedModel.Container<>(model.getClass(), transformer);
        builders.forEach(builder -> builder.accept(model, container));
        return factory.apply(container);
    }
}
