package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import java.io.IOException;

@FunctionalInterface
public interface ChunkFunction<T, R> {

    R apply(T t) throws IOException;
}
