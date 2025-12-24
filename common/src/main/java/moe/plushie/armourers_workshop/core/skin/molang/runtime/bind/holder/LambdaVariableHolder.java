package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.holder;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.LambdaVariableBinding;

import java.util.Stack;

public class LambdaVariableHolder extends ConstantHolder {

    private static int CACHED_SEED = 1;
    private static int CACHED_VERSION = 0;
    private static final Stack<Integer> CACHED_STACK = new Stack<>();

    private int lastVersion = 0;
    private Result lastResult = Result.NULL;
    private ExecutionContext lastContext = null;

    private final LambdaVariableBinding impl;

    public LambdaVariableHolder(LambdaVariableBinding impl) {
        this.impl = impl;
    }

    public static void push() {
        CACHED_SEED += 2;
        CACHED_STACK.push(CACHED_VERSION);
        CACHED_VERSION = CACHED_SEED;
    }

    public static void pop() {
        if (CACHED_STACK.isEmpty()) {
            CACHED_VERSION = 0;
            return; // the push/pop calls no match!!!
        }
        CACHED_VERSION = CACHED_STACK.pop();
    }


    @Override
    public Result evaluate(final ExecutionContext context) {
        // when version is 0 means disabled cache.
        int version = CACHED_VERSION;
        if (version == 0) {
            return impl.evaluate(context);
        }
        // hit fast caching?
        if (version == lastVersion && context == lastContext) {
            return lastResult;
        }
        var result = impl.evaluate(context);
        lastContext = context;
        lastResult = result;
        lastVersion = version;
        return result;
    }
}
