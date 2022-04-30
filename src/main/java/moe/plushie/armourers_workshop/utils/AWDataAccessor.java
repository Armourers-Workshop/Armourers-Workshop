package moe.plushie.armourers_workshop.utils;

import net.minecraft.network.datasync.IDataSerializer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class AWDataAccessor<S, T> {

    protected Function<S, T> supplier;
    protected BiConsumer<S, T> applier;

    public IDataSerializer<T> dataSerializer;


    public static <S, T> AWDataAccessor<S, T> of(IDataSerializer<T> dataSerializer, Function<S, T> supplier, BiConsumer<S, T> applier) {
        AWDataAccessor<S, T> dataAccessor = new AWDataAccessor<>();
        dataAccessor.dataSerializer = dataSerializer;
        dataAccessor.supplier = supplier;
        dataAccessor.applier = applier;
        return dataAccessor;
    }

    public static <S, T> AWDataAccessor<S, T> withDataSerializer(Class<S> clazz, IDataSerializer<T> dataSerializer) {
        AWDataAccessor<S, T> dataAccessor = new AWDataAccessor<>();
        dataAccessor.dataSerializer = dataSerializer;
        return dataAccessor;
    }

    public AWDataAccessor<S, T> withApplier(BiConsumer<S, T> applier) {
        this.applier = applier;
        return this;
    }

    public AWDataAccessor<S, T> withSupplier(Function<S, T> supplier) {
        this.supplier = supplier;
        return this;
    }

    public void set(S s, T v) {
        if (applier != null) {
            applier.accept(s, v);
        }
    }

    public T get(S s) {
        if (supplier != null) {
            return supplier.apply(s);
        }
        return null;
    }
}
