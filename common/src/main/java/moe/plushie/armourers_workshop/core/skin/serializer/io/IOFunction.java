package moe.plushie.armourers_workshop.core.skin.serializer.io;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<T, R> {

    R apply(T t) throws IOException;
}
