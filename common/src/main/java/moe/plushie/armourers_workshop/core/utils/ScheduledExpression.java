package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;

public interface ScheduledExpression<T> extends OptimizedExpression<ScheduledExpression<T>> {

    T submit(final ExecutionContext context);

    default void cancel(T result){
        // nop.
    }

    @Override
    default ScheduledExpression<T> evaluate(final ExecutionContext context) {
        return this;
    }
}
