package moe.plushie.armourers_workshop.core.utils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface OpenResourceManager {

    boolean hasResource(OpenResourceKey key);

    OpenResource readResource(OpenResourceKey key) throws IOException;

    void listResources(OpenResourceKey target, Predicate<String> validator, BiConsumer<OpenResourceKey, OpenResource> consumer);


    interface PreparableReloadListener {

        CompletableFuture<Void> reload(OpenResourceManager resourceManager, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor);

        interface PreparationBarrier {
            <T> CompletableFuture<T> wait(T t);
        }
    }
}
