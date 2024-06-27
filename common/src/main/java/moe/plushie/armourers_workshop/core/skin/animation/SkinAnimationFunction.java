package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * Functional interface defining an easing function
 *
 * @see <a href="https://easings.net/">Easings.net</a>
 * @see <a href="https://cubic-bezier.com">Cubic-Bezier.com</a>
 */
public abstract class SkinAnimationFunction {

    public static SkinAnimationFunction step() {
        return Step.INSTANCE;
    }

    public static SkinAnimationFunction linear() {
        return Linear.INSTANCE;
    }

    public static SkinAnimationFunction smooth() {
        return Smooth.INSTANCE;
    }

    public static SkinAnimationFunction bezier(float[] parameters) {
        return new Bezier(parameters);
    }

    public static SkinAnimationFunction bezier(List<Float> parameters) {
        var values = new float[12];
        for (int i = 0; i < values.length; ++i) {
            if (parameters != null && i < parameters.size()) {
                values[i] = parameters.get(i);
            } else {
                values[i] = 0;
            }
        }
        return new Bezier(values);
    }

    public static SkinAnimationFunction readFromStream(IInputStream stream) throws IOException {
        return switch (stream.readVarInt()) {
            case 3 -> bezier(stream.readFloatArray(12));
            case 2 -> smooth();
            case 1 -> step();
            case 0 -> linear();
            default -> linear(); // can't parse, downcast.
        };
    }

    public abstract float apply(float t);

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    /**
     * A stepped value based on the nearest step to the input value
     */
    public static class Step extends SkinAnimationFunction {

        private static final Step INSTANCE = new Step();

        @Override
        public float apply(float t) {
            return 1; // 0 or 1
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeVarInt(1);
        }
    }

    /**
     * A Linear interpolation Function. Functionally equivalent to no easing
     */
    public static class Linear extends SkinAnimationFunction {

        private static final Linear INSTANCE = new Linear();

        @Override
        public float apply(float t) {
            return t;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeVarInt(0);
        }
    }

    /**
     * A Smooth interpolation function, used to get smooth interpolated motion between keyframes
     * <p>
     *
     * <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">Bézier Curve</a><p/>
     * <a href="https://github.com/mrdoob/three.js/blob/master/src/extras/core/Interpolations.js">CatmullRi om</a>
     */
    public static class Smooth extends SkinAnimationFunction {

        private static final Smooth INSTANCE = new Smooth();

        @Override
        public float apply(float t) {
            if (t < 0.5f) {
                return applyEaseIn(t * 2f) / 2f;
            }
            return 1f - applyEaseIn((1f - t) * 2f) / 2f;
        }

        public float applyEaseIn(float t) {
            return (0.5f * (2.0f * (t + 1) + ((t + 2) - t) * 1
                    + (2.0f * t - 5.0f * (t + 1) + 4.0f * (t + 2) - (t + 3)) * 1
                    + (3.0f * (t + 1) - t - 3.0f * (t + 2) + (t + 3)) * 1));
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeVarInt(2);
        }
    }

    public static class Bezier extends SkinAnimationFunction {

        private final float[] parameters;

        public Bezier(float[] parameters) {
            this.parameters = parameters;
        }

        @Override
        public float apply(float t) {
            // TODO: NO IMPL
            // https://github.com/JannisX11/blockbench/blob/ae59908f986df984fa1b8b4c8fa42a476ee703d9/js/animations/keyframe.js#L228
            // https://github.com/bernie-g/geckolib/blob/14413e9d1d3637c2bfb9c3e03d6911ed785c37f0/common/src/main/java/software/bernie/geckolib/animation/EasingType.java
            return t;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeVarInt(3);
            stream.writeFloatArray(parameters);
        }
    }
}
