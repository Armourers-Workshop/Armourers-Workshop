package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.Consumer;

/** Owns separate GPU targets for layers whose blits are deferred until preparation completes. */
final class NativeGuiLayers<T> {
    private final Map<String, T> targets = new HashMap<>();
    T target(String key, Supplier<T> allocate) { return targets.computeIfAbsent(key, ignored -> allocate.get()); }
    void close(Consumer<T> release) { targets.values().forEach(release); targets.clear(); }
}
