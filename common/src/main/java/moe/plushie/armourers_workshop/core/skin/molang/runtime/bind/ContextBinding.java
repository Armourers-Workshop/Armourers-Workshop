package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.NamedObject;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.holder.FunctionHolder;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.holder.LambdaVariableHolder;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.holder.StaticVariableHolder;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.BlockEntityVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.ContextVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.EntityVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.LevelVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.LivingEntityVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.PlayerVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.ProjectileEntityVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextBinding extends NamedObject implements ObjectBinding {

    private final Map<String, Expression> children = new ConcurrentHashMap<>();

    public void constant(String name, boolean value) {
        constant(name, Result.valueOf(value));
    }

    public void constant(String name, double value) {
        constant(name, Result.valueOf(value));
    }

    public void constant(String name, String value) {
        constant(name, Result.valueOf(value));
    }

    public void constant(String name, Result value) {
        put(name, new StaticVariableHolder(value));
    }

    public void variable(String name, ContextVariableBinding holder) {
        put(name, new LambdaVariableHolder(holder));
    }

    public void variable(String name, LevelVariableBinding holder) {
        put(name, new LambdaVariableHolder(holder));
    }

    public void variable(String name, EntityVariableBinding holder) {
        put(name, new LambdaVariableHolder(holder));
    }

    public void variable(String name, LivingEntityVariableBinding holder) {
        put(name, new LambdaVariableHolder(holder));
    }

    public void variable(String name, PlayerVariableBinding holder) {
        put(name, new LambdaVariableHolder(holder));
    }

    public void variable(String name, ProjectileEntityVariableBinding holder) {
        put(name, new LambdaVariableHolder(holder));
    }

    public void variable(String name, BlockEntityVariableBinding holder) {
        put(name, new LambdaVariableHolder(holder));
    }

    public void function(String name, Function.Factory<?> factory) {
        put(name, new FunctionHolder(factory));
    }


    public void add(String name, ObjectBinding binding) {
        put(name, binding);
    }

    public void alias(String oldName, String newName) {
        var expr = children.get(oldName);
        if (expr != null) {
            children.put(newName, expr);
        }
    }

    @Override
    public Expression propertyByName(String name) {
        var key = name.toLowerCase();
        return children.get(key);
    }

    protected void put(String name, Expression value) {
        var key = name.toLowerCase();
        children.put(key, value);
        if (value instanceof NamedObject namedObject) {
            namedObject.setName(key, this);
        }
    }
}
