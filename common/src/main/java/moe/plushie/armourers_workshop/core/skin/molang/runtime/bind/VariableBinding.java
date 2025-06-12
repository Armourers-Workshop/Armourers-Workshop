package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.NamedObject;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.holder.ConstantHolder;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.holder.VariableHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class VariableBinding extends NamedObject implements ObjectBinding {

    protected final Map<String, Expression> children = new ConcurrentHashMap<>();

    protected abstract Expression build(Name name);

    @Override
    public Expression propertyByName(String name) {
        return children.computeIfAbsent(name.toLowerCase(), it -> {
            var holder = build(Name.of(it));
            if (holder instanceof NamedObject namedObject) {
                namedObject.setName(it, this);
            }
            return holder;
        });
    }

    /**
     * Context variables (eg: `context.moo`) are read-only and specified by the game in certain situations.
     * Details on what variables are specified and when will be available in the documentation of the area where that Molang expression is used (such as behaviors defining what context variables they expose).
     */
    public static class Foreign extends VariableBinding {

        @Override
        protected Expression build(Name name) {
            return new ConstantHolder() {

                @Override
                public Result evaluate(final ExecutionContext context) {
                    return context.getVariable(name);
                }
            };
        }
    }

    /**
     * Entity variables (eg: `variable.moo = 1;`) are read/write and store their value on the entity for the lifetime of that entity.
     * Note that these are currently not saved, so quitting and reloading the world will re-initialize these.
     * In the same way, if the entity is despawned, any variables on the entity will be lost.
     */
    public static class Scoped extends VariableBinding {

        @Override
        protected Expression build(Name name) {
            return new VariableHolder() {

                @Override
                public Result assign(final Result value, final ExecutionContext context) {
                    var entity = context.entity();
                    if (entity == null) {
                        return Result.NULL;
                    }
                    entity.setVariable(name, value);
                    return value;
                }

                @Override
                public Result evaluate(final ExecutionContext context) {
                    var entity = context.entity();
                    if (entity == null) {
                        return Result.NULL;
                    }
                    return entity.getVariable(name);
                }
            };
        }
    }

    /**
     * Temporary variables (eg: `temp.moo = 1;`) are read/write and valid for the scope they are defined in, as per C rules.
     * For performance reasons their lifetime is global to the current expression execution and may return a valid value outside of the outermost scope they are defined in for an expression. Be careful in complex expressions.
     * We will be adding content errors for invalid accesses as soon as possible.
     */
    public static class Temp extends VariableBinding {

        private final Map<Name, Integer> addresses = new ConcurrentHashMap<>();

        @Override
        protected Expression build(Name name) {
            int address = addresses.computeIfAbsent(name, it -> addresses.size());
            return new VariableHolder() {

                @Override
                public Result assign(final Result value, final ExecutionContext context) {
                    context.stack().setVariable(address, value);
                    return value;
                }

                @Override
                public Result evaluate(final ExecutionContext context) {
                    return context.stack().getVariable(address);
                }
            };
        }
    }
}
