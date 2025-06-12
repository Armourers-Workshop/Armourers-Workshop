package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.StaticVariableStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityVariableStorageImpl {

    private final Map<Integer, StaticVariableStorage> storage = new ConcurrentHashMap<>();

    public StaticVariableStorage get(ContextSelectorImpl context) {
        return storage.computeIfAbsent(context.id(), it -> new StaticVariableStorage());
    }
}
