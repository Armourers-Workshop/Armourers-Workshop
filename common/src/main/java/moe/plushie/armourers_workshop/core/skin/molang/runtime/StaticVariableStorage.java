package moe.plushie.armourers_workshop.core.skin.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.VariableStorage;

import java.util.HashMap;
import java.util.Map;

public class StaticVariableStorage implements VariableStorage {

    protected final Map<Name, Result> elements = new HashMap<>();

    @Override
    public void setVariable(Name name, Result value) {
        elements.put(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return elements.getOrDefault(name, Result.NULL);
    }

    public StaticVariableStorage copy() {
        var storage = new StaticVariableStorage();
        storage.elements.putAll(elements);
        return storage;
    }
}

