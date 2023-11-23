package moe.plushie.armourers_workshop.core.skin.serializer.io;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer2<T1, T2> {

    void accept(T1 t1, T2 t2) throws IOException;
}
