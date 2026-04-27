package moe.plushie.armourers_workshop.core.skin.animation;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationEffect;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationTransform;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SkinAnimation {

    private final int id = OpenRandomSource.nextInt(SkinAnimation.class);

    private final String name;

    private final double duration;
    private final SkinAnimationData.Loop loop;

    private final List<Animator<?>> animators;
    private final List<SkinAnimationEffect> affectedEffects;
    private final List<SkinAnimationTransform> affectedTransforms;

    private final List<Output<?>> outputs;

    private final Mode mode;

    private final boolean isRequiresVirtualMachine;

    public SkinAnimation(String name, double duration, SkinAnimationData.Loop loop, List<Animator<?>> animators) {
        this.name = name;

        this.loop = loop;
        this.duration = duration;

        this.mode = Mode.parse(name);

        this.animators = animators;
        this.affectedEffects = new ArrayList<>();
        this.affectedTransforms = new ArrayList<>();

        this.outputs = Collections.compactMap(animators, animator -> {
            var bone = animator.bone();
            var channel = animator.channel();
            animator.output = new Output<>(bone, channel, mode);
            return animator.output;
        });

        this.isRequiresVirtualMachine = calcRequiresVirtualMachine();
    }

    public static int toTime(double time) {
        return (int) (time * 1000);
    }

    public void process(double time, ExecutionContext context) {
        var realTime = wrapTime(time);
        for (var animator : animators) {
            animator.process(realTime, context);
        }
    }

    public String name() {
        return name;
    }

    public SkinAnimationData.Loop loop() {
        return loop;
    }

    public double duration() {
        return duration;
    }

    public boolean isEmpty() {
        return animators.isEmpty();
    }

    public boolean isParallel() {
        return mode.isParallel();
    }

    public boolean isRequiresVirtualMachine() {
        return isRequiresVirtualMachine;
    }

    public List<Output<?>> outputs() {
        return outputs;
    }

    public List<Animator<?>> animators() {
        return animators;
    }

    public List<SkinAnimationEffect> affectedEffects() {
        return affectedEffects;
    }

    public List<SkinAnimationTransform> affectedTransforms() {
        return affectedTransforms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinAnimation that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "duration", duration, "loop", loop);
    }

    private int wrapTime(double time) {
        if (loop == SkinAnimationData.Loop.LOOP) {
            if (time > duration) {
                return toTime(time % duration);
            }
        }
        return toTime(time);
    }

    private boolean calcRequiresVirtualMachine() {
        for (var animator : animators) {
            if (!animator.isConstant()) {
                return true;
            }
        }
        return false;
    }

    /**
     * A skin animation output mode.
     */
    public enum Mode {

        PRE_MAIN(-100), MAIN(0), POST_MAIN(100);

        private final int priority;

        Mode(int priority) {
            this.priority = priority;
        }

        public static Mode parse(String name) {
            if (name.matches("^(.+\\.)?pre_parallel(\\d+)$")) {
                return PRE_MAIN;
            }
            if (name.matches("^(.+\\.)?parallel(\\d+)$")) {
                return POST_MAIN;
            }
            return MAIN;
        }

        public int priority() {
            return priority;
        }

        public boolean isMixMode() {
            return this == POST_MAIN;
        }

        public boolean isParallel() {
            return this != MAIN;
        }
    }

    /**
     * Enum representing various animation or effect channels that can be used
     * within the context of a SkinAnimation system.
     * Each channel corresponds to a specific type of transform or effect behavior.
     */
    public enum Channel {

        TRANSLATION("position"), ROTATION("rotation"), SCALE("scale"), PARTICLE("particle"), SOUND("sound"), INSTRUCT("timeline");

        private final String key;

        Channel(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }

        public boolean isBone() {
            return this == TRANSLATION || this == ROTATION || this == SCALE;
        }

        public boolean isEffect() {
            return this == PARTICLE || this == SOUND || this == INSTRUCT;
        }
    }

    /**
     * Base runtime animator that evaluates keyframes and applies the result to a target channel.
     */
    public static class Animator<T> {

        private final String bone;
        private final Channel channel;
        private final List<Keyframe<T>> keyframes;

        private int cursor = 0;
        private Output<T> output;

        public Animator(String bone, Channel channel, List<Keyframe<T>> keyframes) {
            this.bone = bone;
            this.channel = channel;
            this.keyframes = keyframes;
        }

        public void process(int time, ExecutionContext context) {
            var frame = frameAtTime(time);
            var result = frame.evaluate(time, context);
            // write a result into the output slot if it needs.
            if (output != null) {
                output.set(result);
            }
        }

        public Keyframe<T> frameAtTime(int time) {
            if (keyframes.isEmpty()) {
                throw new IllegalStateException("keyframes must not be empty");
            }
            var keyframe = keyframes.get(cursor);
            if (time < keyframe.fromTime() && cursor > 0) {
                cursor = search(time, 0, cursor);
                return keyframes.get(cursor);
            }
            if (time >= keyframe.toTime() && cursor + 1 < keyframes.size()) {
                cursor = search(time, cursor + 1, keyframes.size());
                return keyframes.get(cursor);
            }
            return keyframe;
        }

        public String bone() {
            return bone;
        }

        public Channel channel() {
            return channel;
        }

        public List<Keyframe<T>> keyframes() {
            return keyframes;
        }

        public boolean isEmpty() {
            return keyframes.isEmpty();
        }

        public boolean isConstant() {
            for (var keyframe : keyframes) {
                if (!keyframe.isConstant()) {
                    return false;
                }
            }
            return true;
        }

        /// [start, end)
        private int search(int time, int start, int end) {
            for (int index = start; index < end; ++index) {
                var keyframe = keyframes.get(index);
                if (time < keyframe.toTime()) {
                    return index;
                }
            }
            return end - 1;
        }
    }

    /**
     * Interpolates values between two keyframe endpoints.
     */
    public static class Interpolator<T> {

        /**
         * Returns a none interpolator (like step, but not lrep).
         */
        public static Interpolator<Object> none() {
            return None.INSTANCE;
        }

        /**
         * Returns a step interpolator.
         */
        public static Interpolator<OpenVector3f> step() {
            return Step.INSTANCE;
        }

        /**
         * Returns a linear interpolator.
         */
        public static Interpolator<OpenVector3f> linear() {
            return Linear.INSTANCE;
        }

        /**
         * Returns a Bezier interpolator using the specified control points.
         */
        public static Interpolator<OpenVector3f> bezier(OpenVector3f t0, OpenVector3f p0, OpenVector3f t1, OpenVector3f p1) {
            return new Bezier(t0, p0, t1, p1);
        }

        /**
         * Returns a smooth interpolator that generates a Catmull-Rom spline
         * interpolation for 3D vector points.
         */
        public static Interpolator<OpenVector3f> smooth() {
            return CatmullRom.INSTANCE;
        }

        /**
         * Interpolates a value at a given progress ratio `t` within the context of a specified keyframe.
         * <p>
         * The method determines the interpolated value based on the provided progress ratio:
         * - Returns the starting value of the keyframe if `t` is less than or equal to 0.
         * - Returns the ending value of the keyframe if `t` is greater than or equal to 1.
         * - Computes an interpolated value for `t` within the range (0, 1) using an internal logic.
         *
         * @param t       The progress ratio, a float between 0 and 1, representing the interpolation position.
         * @param frame   The keyframe which describes the interval and values being interpolated.
         * @param context The execution context used for evaluating keyframe values or expressions.
         * @return The interpolated value of type `T` based on the input progress ratio `t` and keyframe details.
         */
        public final T interpolate(float t, Keyframe<T> frame, ExecutionContext context) {
            if (t <= 0.0f) {
                return frame.fromValue(context);
            }
            if (t >= 1.0f) {
                return frame.toValue(context);
            }
            return _applyValue(t, frame, context);
        }

        protected T _applyValue(float t, Keyframe<T> frame, ExecutionContext context) {
            return frame.fromValue(context);
        }

        /**
         * No interpolation.
         */
        private static class None extends Interpolator<Object> {

            private static final None INSTANCE = new None();
        }

        /**
         * Step interpolation that snaps to the start value until the end of the segment.
         */
        private static class Step extends Interpolator<OpenVector3f> {

            private static final Step INSTANCE = new Step();
        }

        /**
         * Linear interpolation between keyframe endpoints.
         *
         * @see <a href="https://easings.net/">Easings.net</a>
         * @see <a href="https://cubic-bezier.com">Cubic-Bezier.com</a>
         */
        private static class Linear extends Interpolator<OpenVector3f> {

            private static final Linear INSTANCE = new Linear();

            @Override
            protected OpenVector3f _applyValue(float t, Keyframe<OpenVector3f> frame, ExecutionContext context) {
                var fromValue = frame.fromValue(context);
                var toValue = frame.toValue(context);
                var x = OpenMath.lerp(t, fromValue.x(), toValue.x());
                var y = OpenMath.lerp(t, fromValue.y(), toValue.y());
                var z = OpenMath.lerp(t, fromValue.z(), toValue.z());
                return new OpenVector3f(x, y, z);
            }
        }

        /**
         * Cubic-bezier interpolation with per-axis lookup acceleration.
         */
        private static class Bezier extends Interpolator<OpenVector3f> {

            private final OpenVector3f p0;
            private final OpenVector3f p1;

            private final Float2FloatFunction lookupX;
            private final Float2FloatFunction lookupY;
            private final Float2FloatFunction lookupZ;

            private Bezier(OpenVector3f t0, OpenVector3f p0, OpenVector3f t1, OpenVector3f p1) {
                this.p0 = p0;
                this.p1 = p1;
                this.lookupX = lookup(0.0f, t0.x(), t1.x(), 1.0f, 255);
                this.lookupY = lookup(0.0f, t0.y(), t1.y(), 1.0f, 255);
                this.lookupZ = lookup(0.0f, t0.z(), t1.z(), 1.0f, 255);
            }

            @Override
            protected OpenVector3f _applyValue(float t, Keyframe<OpenVector3f> frame, ExecutionContext context) {
                var fromValue = frame.fromValue(context);
                var toValue = frame.toValue(context);
                var x = cubic(lookupX.get(t), fromValue.x(), fromValue.x() + p0.x(), toValue.x() + p1.x(), toValue.x());
                var y = cubic(lookupY.get(t), fromValue.y(), fromValue.y() + p0.y(), toValue.y() + p1.y(), toValue.y());
                var z = cubic(lookupZ.get(t), fromValue.z(), fromValue.z() + p0.z(), toValue.z() + p1.z(), toValue.z());
                return new OpenVector3f(x, y, z);
            }

            private float cubic(float t, float p0, float p1, float p2, float p3) {
                var k = 1.0f - t;
                return k * k * k * p0 + 3.0f * k * k * t * p1 + 3.0f * k * t * t * p2 + t * t * t * p3;
            }

            private float cubicDerivative(float t, float p0, float p1, float p2, float p3) {
                var k = 1.0f - t;
                return 3.0f * k * k * (p1 - p0) + 6.0f * k * t * (p2 - p1) + 3.0f * t * t * (p3 - p2);
            }

            private float solveCurveT(float x, float p0, float p1, float p2, float p3) {
                var t = x;
                for (int i = 0; i < 8; ++i) {
                    var value = cubic(t, p0, p1, p2, p3) - x;
                    var slope = cubicDerivative(t, p0, p1, p2, p3);
                    if (Math.abs(value) < 1.0e-5f || Math.abs(slope) < 1.0e-6f) {
                        break;
                    }
                    t = OpenMath.clamp(t - value / slope, 0.0f, 1.0f);
                }
                var low = 0.0f;
                var high = 1.0f;
                for (int i = 0; i < 10; ++i) {
                    var value = cubic(t, p0, p1, p2, p3);
                    if (Math.abs(value - x) < 1.0e-4f) {
                        break;
                    }
                    if (value < x) {
                        low = t;
                    } else {
                        high = t;
                    }
                    t = (low + high) * 0.5f;
                }
                return t;
            }

            private Float2FloatFunction lookup(float p0x, float p1x, float p2x, float p3x, int size) {
                var table = new float[size + 1];
                for (int i = 0; i <= size; ++i) {
                    var x = i / (float) size;
                    table[i] = solveCurveT(x, p0x, p1x, p2x, p3x);
                }
                return x -> {
                    var fx = OpenMath.clamp(x, 0.0f, 1.0f) * size;
                    var index = (int) fx;
                    if (index >= size) {
                        return table[size];
                    }
                    var nextIndex = index + 1;
                    var k = fx - index;
                    var u = OpenMath.lerp(k, table[index], table[nextIndex]);
                    var value = cubic(u, p0x, p1x, p2x, p3x) - x;
                    var slope = cubicDerivative(u, p0x, p1x, p2x, p3x);
                    if (Math.abs(slope) > 1.0e-6f) {
                        u = OpenMath.clamp(u - value / slope, 0.0f, 1.0f);
                    }
                    return u;
                };
            }
        }

        /**
         * Catmull-Rom spline interpolation using neighbor keyframe values.
         */
        private static class CatmullRom extends Interpolator<OpenVector3f> {

            private static final CatmullRom INSTANCE = new CatmullRom();

            @Override
            protected OpenVector3f _applyValue(float t, Keyframe<OpenVector3f> frame, ExecutionContext context) {
                var previousValue = frame.previousValue(context);
                var fromValue = frame.fromValue(context);
                var toValue = frame.toValue(context);
                var nextValue = frame.nextValue(context);
                // skip evaluate when previous value not provided.
                if (previousValue == null) {
                    previousValue = fromValue;
                }
                // skip evaluate when next value not provided.
                if (nextValue == null) {
                    nextValue = toValue;
                }
                var x = OpenMath.catmullrom(t, previousValue.x(), fromValue.x(), toValue.x(), nextValue.x());
                var y = OpenMath.catmullrom(t, previousValue.y(), fromValue.y(), toValue.y(), nextValue.y());
                var z = OpenMath.catmullrom(t, previousValue.z(), fromValue.z(), toValue.z(), nextValue.z());
                return new OpenVector3f(x, y, z);
            }
        }
    }

    /**
     * Represents a generic output container used in the skin animation system.
     */
    public static class Output<T> {

        private T value;

        private final String bone;
        private final Channel channel;
        private final Mode mode;

        private Runnable listener;

        public Output(String bone, Channel channel, Mode mode) {
            this.bone = bone;
            this.channel = channel;
            this.mode = mode;
        }

        public void addListener(Runnable listener) {
            this.listener = listener;
        }

        public void removeListener(Runnable listener) {
            this.listener = null;
        }

        public void reset() {
            this.value = null;
        }

        public void set(T value) {
            this.value = value;
            // apply the change into the affected transform.
            if (this.listener != null) {
                this.listener.run();
            }
        }

        public T get() {
            return value;
        }

        public String bone() {
            return bone;
        }

        public Mode mode() {
            return mode;
        }

        public Channel channel() {
            return channel;
        }

        public boolean isBone() {
            return channel.isBone();
        }

        public boolean isEffect() {
            return channel.isEffect();
        }

        public int priority() {
            return mode.priority();
        }
    }

    /**
     * Immutable keyframe segment describing an interval, endpoint expressions, and interpolation strategy.
     */
    public static class Keyframe<T> {

        private final int fromTime;
        private final int toTime;

        private final OptimizedExpression<T> fromValue;
        private final OptimizedExpression<T> toValue;

        private final @Nullable OptimizedExpression<T> previousValue;
        private final @Nullable OptimizedExpression<T> nextValue;

        private final float duration;
        private final Interpolator<T> interpolator;

        public Keyframe(int fromTime, int toTime, OptimizedExpression<T> fromValue, OptimizedExpression<T> toValue, Interpolator<T> interpolator) {
            this(fromTime, toTime, null, fromValue, toValue, null, interpolator);
        }

        public Keyframe(int fromTime, int toTime, @Nullable OptimizedExpression<T> previousValue, OptimizedExpression<T> fromValue, OptimizedExpression<T> toValue, @Nullable OptimizedExpression<T> nextValue, Interpolator<T> interpolator) {
            this.fromTime = fromTime;
            this.fromValue = fromValue;
            this.toTime = toTime;
            this.toValue = toValue;
            this.previousValue = previousValue;
            this.nextValue = nextValue;
            this.duration = Math.max(toTime - fromTime, 1); // always non-zero
            this.interpolator = interpolator;
        }

        public boolean contains(int time) {
            return fromTime <= time && time < toTime;
        }

        public T evaluate(int time, ExecutionContext context) {
            var delta = (time - fromTime) / duration;
            return interpolator.interpolate(delta, this, context);
        }

        public int fromTime() {
            return fromTime;
        }

        public int toTime() {
            return toTime;
        }

        public T fromValue(ExecutionContext context) {
            return fromValue.evaluate(context);
        }

        public T toValue(ExecutionContext context) {
            return toValue.evaluate(context);
        }

        @Nullable
        public T previousValue(ExecutionContext context) {
            if (previousValue != null) {
                return previousValue.evaluate(context);
            }
            return null;
        }

        @Nullable
        public T nextValue(ExecutionContext context) {
            if (nextValue != null) {
                return nextValue.evaluate(context);
            }
            return null;
        }

        public boolean isConstant() {
            if (previousValue != null && !previousValue.isConstant()) {
                return false;
            }
            if (nextValue != null && !nextValue.isConstant()) {
                return false;
            }
            return fromValue.isConstant() && toValue.isConstant();
        }
    }
}
