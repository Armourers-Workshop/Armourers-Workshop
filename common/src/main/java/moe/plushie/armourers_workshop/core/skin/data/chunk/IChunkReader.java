package moe.plushie.armourers_workshop.core.skin.data.chunk;

public interface IChunkReader {
    <T> T read(IChunkType type);
}
