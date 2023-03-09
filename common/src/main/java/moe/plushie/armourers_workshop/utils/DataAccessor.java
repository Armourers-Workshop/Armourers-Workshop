package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataAccessor<S, T> {

    public IEntitySerializer<T> dataSerializer;
    protected Function<S, T> supplier;
    protected BiConsumer<S, T> applier;

    public static <S, T> DataAccessor<S, T> of(IEntitySerializer<T> dataSerializer, Function<S, T> supplier, BiConsumer<S, T> applier) {
        DataAccessor<S, T> dataAccessor = new DataAccessor<>();
        dataAccessor.dataSerializer = dataSerializer;
        dataAccessor.supplier = supplier;
        dataAccessor.applier = applier;
        return dataAccessor;
    }

    public static <S, T> DataAccessor<S, T> withDataSerializer(Class<S> clazz, IEntitySerializer<T> dataSerializer) {
        DataAccessor<S, T> dataAccessor = new DataAccessor<>();
        dataAccessor.dataSerializer = dataSerializer;
        return dataAccessor;
    }

    public DataAccessor<S, T> withApplier(BiConsumer<S, T> applier) {
        this.applier = applier;
        return this;
    }

    public DataAccessor<S, T> withSupplier(Function<S, T> supplier) {
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
