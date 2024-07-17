package moe.plushie.armourers_workshop.api.data;

public interface IDataSerializer {

    <T> T read(IDataSerializerKey<T> key);

    <T> void write(IDataSerializerKey<T> key, T value);
}
