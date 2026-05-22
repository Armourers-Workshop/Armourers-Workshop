package moe.plushie.armourers_workshop.core.skin.molang.core;

/**
 * The expression interface. It's the super-interface for
 * all the expression types.
 *
 * <p>Expressions are evaluable parts of code, expressions
 * are emitted by the parser.</p>
 *
 * <p>In Molang, almost every expression evaluates to a numerical
 * value</p>
 */
public interface Expression {

    /**
     * Evaluates (interprets) the expressions and returns a single
     * value, commonly, a double value.
     *
     * @param context The execution context
     * @return The evaluates result
     */
    Result evaluate(final ExecutionContext context);

    /**
     * Evaluates (interprets) the expressions and test result.
     *
     * @param context The execution context
     * @return The evaluates result
     */
    default boolean test(final ExecutionContext context) {
        return evaluate(context).booleanValue();
    }

    /**
     * Evaluates (interprets) the expressions and returns double.
     *
     * @param context The execution context
     * @return The evaluates result
     */
    default double compute(final ExecutionContext context) {
        return evaluate(context).doubleValue();
    }

    /**
     * Visits this expression with the given visitor.
     *
     * @param visitor The expression visitor
     * @return The visit result
     */
    default Expression visit(final Visitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Return whether this type of MathValue should be considered mutable; its value could change.
     * <br>
     * This is used to cache calculated values, optimising computational work
     */
    default boolean isMutable() {
        return true;
    }
}
