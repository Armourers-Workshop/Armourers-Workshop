package moe.plushie.armourers_workshop.core.skin.serializer.io;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {

    void accept(T t) throws IOException;
}

