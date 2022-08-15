package moe.plushie.armourers_workshop.core.skin.data.chunk;

public interface IChunkWriter {
    <T> void write(IChunkType type, T value);
}
