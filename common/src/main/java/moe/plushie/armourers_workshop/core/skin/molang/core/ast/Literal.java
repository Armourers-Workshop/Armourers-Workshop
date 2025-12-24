package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;

public final class Literal implements Expression {

    private final Result value;

    public Literal(String value) {
        this.value = Result.valueOf(value);
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        return value;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitLiteral(this);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("'%s'", value.toString());
    }
}
