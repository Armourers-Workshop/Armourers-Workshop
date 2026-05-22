package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.ComputedResult;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.PrettyPrinter;

/**
 * Unary expression implementation, performs a single operation
 * to a single expression, like logical negation, arithmetical
 * negation
 *
 * <p>Example unary expressions: {@code -hello}, {@code !p},
 * {@code !q}, {@code -(10 * 5)}</p>
 */
public final class Unary implements Expression, Optimizable {

    private final Operator op;
    private final Expression value;

    public Unary(Operator op, Expression value) {
        this.op = op;
        this.value = value;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        return op.evaluator.eval(context, value);
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitUnary(this);
    }

    @Override
    public boolean isMutable() {
        return value.isMutable();
    }

    @Override
    public String toString() {
        return PrettyPrinter.toString(this);

    }

    public Operator op() {
        return op;
    }

    public Expression value() {
        return value;
    }

    public enum Operator {
        LOGICAL_NEGATION("!", 2800, (context, expr) -> ComputedResult.valueOf(!expr.test(context))),
        ARITHMETICAL_NEGATION("-", 2800, (context, expr) -> ComputedResult.valueOf(-expr.compute(context))),
        ARITHMETICAL_PLUS("+", 2800, null);

        private final String symbol;
        private final Evaluator evaluator;
        private final int precedence;

        Operator(final String symbol, final int precedence, final Evaluator evaluator) {
            this.symbol = symbol;
            this.evaluator = evaluator;
            this.precedence = precedence;
        }

        public String symbol() {
            return symbol;
        }

        public int precedence() {
            return precedence;
        }
    }

    private interface Evaluator {
        /**
         * Computing the mathematical result of input arguments
         *
         * @param context The evaluate context
         * @param value   The first input argument
         * @return The computed value of the two inputs
         */
        Result eval(ExecutionContext context, Expression value);
    }
}
