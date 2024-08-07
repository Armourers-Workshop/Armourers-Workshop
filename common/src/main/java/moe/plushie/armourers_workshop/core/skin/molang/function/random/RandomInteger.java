package moe.plushie.armourers_workshop.core.skin.molang.function.random;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns a random integer value based on the input values:
 * <ul>
 *     <li>A single input generates a value between 0 and that input (exclusive)</li>
 *     <li>Two inputs generates a random value between the first (inclusive) and second input (inclusive)</li>
 *     <li>Three inputs generates a random value between the first (inclusive) and second input (inclusive), seeded by the third input</li>
 * </ul>
 */
public final class RandomInteger extends Function {

    private final Expression valueA;
    @Nullable
    private final Expression valueB;
    @Nullable
    private final Expression seed;
    @Nullable
    private final Random random;

    public RandomInteger(String name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.valueA = arguments[0];
        this.valueB = arguments.size() >= 2 ? arguments.get(1) : null;
        this.seed = arguments.size() >= 3 ? arguments.get(2) : null;
        this.random = this.seed != null ? new Random() : null;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public double getAsDouble() {
        int result;
        int valueA = (int) Math.round(this.valueA.getAsDouble());
        Random random;

        if (this.random != null) {
            this.random.setSeed((long) this.seed.getAsDouble());
            random = this.random;
        } else {
            random = ThreadLocalRandom.current();
        }

        if (this.valueB != null) {
            int valueB = (int) Math.round(this.valueB.getAsDouble());
            int min = Math.min(valueA, valueB);
            int max = Math.max(valueA, valueB);

            result = min + random.nextInt(max + 1 - min);
        } else {
            result = random.nextInt(valueA + 1);
        }

        return result;
    }
}
