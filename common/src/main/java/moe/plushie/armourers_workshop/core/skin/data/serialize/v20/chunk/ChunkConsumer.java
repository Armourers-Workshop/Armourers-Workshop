package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import java.io.IOException;

@FunctionalInterface
public interface ChunkConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws IOException;
}
