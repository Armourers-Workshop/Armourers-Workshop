package moe.plushie.armourers_workshop.core.skin.molang.function.random;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns a random value based on the input values:
 * <ul>
 *     <li>A single input generates a value between 0 and that input (exclusive)</li>
 *     <li>Two inputs generates a random value between the first (inclusive) and second input (exclusive)</li>
 *     <li>Three inputs generates a random value between the first (inclusive) and second input (exclusive), seeded by the third input</li>
 * </ul>
 */
public final class Random extends Function {

    private final Expression valueA;
    @Nullable
    private final Expression valueB;
    @Nullable
    private final Expression seed;
    @Nullable
    private final java.util.Random random;

    public Random(List<Expression> arguments) {
        super("math.random", 1, arguments);
        this.valueA = arguments[0];
        this.valueB = arguments.size() >= 2 ? arguments.get(1) : null;
        this.seed = arguments.size() >= 3 ? arguments.get(2) : null;
        this.random = this.seed != null ? new java.util.Random() : null;
    }

    @Override
    public double getAsDouble() {
        double result;
        double valueA = this.valueA.getAsDouble();

        if (this.random != null) {
            this.random.setSeed((long) this.seed.getAsDouble());

            result = this.random.nextDouble();
        } else {
            result = Math.random();
        }

        if (this.valueB != null) {
            double valueB = this.valueB.getAsDouble();
            double min = Math.min(valueA, valueB);
            double max = Math.max(valueA, valueB);

            result = min + result * (max - min);
        } else {
            result = result * valueA;
        }

        return result;
    }

}
