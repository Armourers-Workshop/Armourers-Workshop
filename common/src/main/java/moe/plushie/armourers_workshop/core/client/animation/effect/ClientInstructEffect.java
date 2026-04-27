package moe.plushie.armourers_workshop.core.client.animation.effect;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ScheduledExpression;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;

public class ClientInstructEffect implements ScheduledExpression<Object> {

    private final Expression expression;

    public ClientInstructEffect(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object submit(ExecutionContext context) {
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("execute {}", this);
        }
        expression.evaluate(context);
        return null;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "expr", expression);
    }
}
