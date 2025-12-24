package moe.plushie.armourers_workshop.core.skin.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LazyVariableStorage extends StaticVariableStorage {

    protected final Map<Name, Supplier<Result>> holders = new HashMap<>();

    public void setVariable(Name name, Supplier<Result> value) {
        holders.put(name, value);
    }

    @Override
    public void setVariable(Name name, Result value) {
        super.setVariable(name, value);
        holders.remove(name);
    }

    @Override
    public Result getVariable(Name name) {
        var supplier = holders.get(name);
        if (supplier != null) {
            return supplier.get();
        }
        return super.getVariable(name);
    }

    @Override
    public LazyVariableStorage copy() {
        var storage = new LazyVariableStorage();
        storage.elements.putAll(elements);
        storage.holders.putAll(holders);
        return storage;
    }
}

