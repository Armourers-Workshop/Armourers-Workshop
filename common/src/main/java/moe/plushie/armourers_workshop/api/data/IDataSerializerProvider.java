package moe.plushie.armourers_workshop.api.data;

public interface IDataSerializerProvider {

    void serialize(IDataSerializer serializer);

    void deserialize(IDataSerializer serializer);
}
