package moe.plushie.armourers_workshop.api.data;

import moe.plushie.armourers_workshop.api.common.IResultHandler;

public interface IDataLoader<K, V> {

    void load(K key, IResultHandler<V> resultHandler);
}
