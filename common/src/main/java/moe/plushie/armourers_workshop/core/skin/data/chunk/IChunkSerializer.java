package moe.plushie.armourers_workshop.core.skin.data.chunk;

public interface IChunkSerializer<T> {

    T decode(IChunkDataReader reader);

    void encode(IChunkDataWriter writer, T value);
}
