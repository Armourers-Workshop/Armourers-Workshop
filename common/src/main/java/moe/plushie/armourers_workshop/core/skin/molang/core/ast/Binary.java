package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.Assignable;
import moe.plushie.armourers_workshop.core.skin.molang.core.ComputedResult;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.MathHelper;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.PrettyPrinter;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * A computed value of lhs and right defined by the contract of the {@link Operator}
 */
public final class Binary implements Expression, Optimizable {

    private final Operator op;
    private final Expression left;
    private final Expression right;
    private final Evaluator evaluator;

    public Binary(Operator op, Expression left, Expression rhs) {
        this.op = op;
        this.left = left;
        this.right = rhs;
        this.evaluator = op.evaluator;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        return evaluator.evaluate(context, left, right);
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitBinary(this);
    }

    @Override
    public boolean isMutable() {
        return left.isMutable() || right.isMutable();
    }

    @Override
    public String toString() {
        return PrettyPrinter.toString(this);
    }

    public Operator op() {
        return op;
    }

    public Expression left() {
        return left;
    }

    public Expression right() {
        return right;
    }

    public enum Operator {

        AND("&&", 1800, logical((lhs, rhs) -> lhs.getAsBoolean() && rhs.getAsBoolean())),
        OR("||", 1600, logical((lhs, rhs) -> lhs.getAsBoolean() || rhs.getAsBoolean())),

        LT("<", 2200, compare((lhs, rhs) -> lhs.doubleValue() < rhs.doubleValue())),
        LTE("<=", 2200, compare((lhs, rhs) -> lhs.doubleValue() <= rhs.doubleValue())),
        GT(">", 2200, compare((lhs, rhs) -> lhs.doubleValue() > rhs.doubleValue())),
        GTE(">=", 2200, compare((lhs, rhs) -> lhs.doubleValue() >= rhs.doubleValue())),

        ADD("+", 2400, arithmetic(MathHelper::add)),
        SUB("-", 2400, arithmetic(MathHelper::sub)),
        MUL("*", 2600, arithmetic(MathHelper::mul)),
        DIV("/", 2600, arithmetic(MathHelper::div)),
        MOD("%", 2600, arithmetic(MathHelper::mod)),
        POW("^", 2600, arithmetic(MathHelper::pow)),

        ARROW("->", 3000, (context, lhs, rhs) -> {
            var result = lhs.evaluate(context);
            if (result.isValid()) {
                return rhs.evaluate(context.fork(result.referenceValue()));
            }
            return Result.NULL;
        }),

        NULL_COALESCE("??", 1200, (context, lhs, rhs) -> {
            var result = lhs.evaluate(context);
            if (result.isValid()) {
                return result;
            }
            return rhs.evaluate(context);
        }),

        ASSIGN("=", 1000, (context, lhs, rhs) -> {
            // we can only assign to values that are accessed.
            if (!(lhs instanceof Assignable variable)) {
                ModLog.warn("Cannot assign a value to {}", lhs);
                return Result.NULL;
            }
            var result = rhs.evaluate(context);
            if (result.isStruct()) {
                result = result.copy();
            }
            return variable.assign(result, context);
        }),

        ADD_ASSIGN("+=", 1000, selfAssignArithmetic(MathHelper::add)),
        SUB_ASSIGN("-=", 1000, selfAssignArithmetic(MathHelper::sub)),
        MUL_ASSIGN("*=", 1000, selfAssignArithmetic(MathHelper::mul)),
        DIV_ASSIGN("/=", 1000, selfAssignArithmetic(MathHelper::div)),
        MOD_ASSIGN("%=", 1000, selfAssignArithmetic(MathHelper::mod)),
        POW_ASSIGN("^=", 1000, selfAssignArithmetic(MathHelper::pow)),

        NULL_COALESCE_ASSIGN("??=", 1000, (context, lhs, rhs) -> {
            var result = lhs.evaluate(context);
            if (result.isValid()) {
                return result;
            }
            if (!(lhs instanceof Assignable variable)) {
                ModLog.warn("Cannot assign a value to {}", lhs);
                return Result.NULL;
            }
            result = rhs.evaluate(context);
            if (result.isStruct()) {
                result = result.copy();
            }
            return variable.assign(result, context);
        }),

        CONDITIONAL("?", 1400, (context, lhs, rhs) -> {
            if (lhs.test(context)) {
                return rhs.evaluate(context);
            }
            return Result.NULL;
        }),

        EQ("==", 2000, compare(Result::equals)),
        NEQ("!=", 2000, compare(Result::notEquals));

        private final String symbol;
        private final Evaluator evaluator;
        private final int precedence;

        Operator(final String symbol, final int precedence, Evaluator evaluator) {
            this.symbol = symbol;
            this.evaluator = evaluator;
            this.precedence = precedence;
        }

        private static Evaluator compare(BiFunction<Result, Result, Boolean> evaluator) {
            return (context, lhs, rhs) -> {
                var result = evaluator.apply(lhs.evaluate(context), rhs.evaluate(context));
                return ComputedResult.valueOf(result);
            };
        }

        private static Evaluator logical(BiFunction<BooleanSupplier, BooleanSupplier, Boolean> evaluator) {
            return (context, lhs, rhs) -> {
                var result = evaluator.apply(() -> lhs.test(context), () -> rhs.test(context));
                return ComputedResult.valueOf(result);
            };
        }

        private static Evaluator arithmetic(BiFunction<Result, Result, Result> evaluator) {
            return (context, lhs, rhs) -> evaluator.apply(lhs.evaluate(context), rhs.evaluate(context));
        }

        private static Evaluator selfAssignArithmetic(BiFunction<Result, Result, Result> evaluator) {
            return (context, lhs, rhs) -> {
                // ..
                if (!(lhs instanceof Assignable variable)) {
                    ModLog.warn("Cannot assign a value to {}", lhs);
                    return Result.NULL;
                }
                var b = rhs.evaluate(context);
                return variable.assign((a) -> evaluator.apply(a, b), context);
            };
        }

        public String symbol() {
            return symbol;
        }

        public int precedence() {
            return precedence;
        }
    }

    public interface Evaluator {

        /**
         * Computing the mathematical result of two input arguments
         *
         * @param context The evaluate context
         * @param lhs     The first input argument
         * @param rhs     The second input argument
         * @return The computed value of the two inputs
         */
        Result evaluate(ExecutionContext context, Expression lhs, Expression rhs);
    }
}
