package moe.plushie.armourers_workshop.api.data;

public interface IExtraDateStorage {

    <T> T getExtraData(IExtraDateStorageKey<T> key);

    <T> void setExtraData(IExtraDateStorageKey<T> key, T value);
}
