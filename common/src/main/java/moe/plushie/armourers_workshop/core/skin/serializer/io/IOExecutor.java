package moe.plushie.armourers_workshop.core.skin.serializer.io;

import java.io.IOException;

@FunctionalInterface
public interface IOExecutor {

    void run() throws IOException;
}
