package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.animation.handler.AnimationInstructHandler;
import moe.plushie.armourers_workshop.core.client.animation.handler.AnimationParticleHandler;
import moe.plushie.armourers_workshop.core.client.animation.handler.AnimationSoundHandler;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationKeyframe;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Constant;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import moe.plushie.armourers_workshop.init.ModLog;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnimationController {

    private final int id = OpenRandomSource.nextInt(AnimationController.class);

    private final String name;
    private final SkinAnimation animation;

    private final double duration;
    private final SkinAnimationLoop loop;

    private final ArrayList<Animator<?>> animators = new ArrayList<>();

    private final AnimatedOutputMode mode;
    private final boolean isRequiresVirtualMachine;

    public AnimationController(SkinAnimation animation, Map<String, SkinPartTransform> partTransforms) {
        this.name = animation.name();
        this.animation = animation;

        this.loop = animation.loop();
        this.duration = animation.duration();

        this.mode = calcMixMode(name);

        // create all animation.
        animation.keyframes().forEach((partName, linkedValues) -> {
            var partTransform = partTransforms.get(partName);
            if (partTransform != null) {
                this.animators.add(new Animator.Bone(partName, AnimationController.toTime(duration), linkedValues, AnimatedTransform.of(partTransform), mode));
            }
            // TODO: remove "effects" in the future (23-rename-effect-part).
            if (partName.equals("armourers:effects") || partName.equals("effects")) {
                this.animators.add(new Animator.Effect(partName, AnimationController.toTime(duration), linkedValues));
            }
        });

        this.isRequiresVirtualMachine = calcRequiresVirtualMachine();
    }

    public static int toTime(double time) {
        return (int) (time * 1000);
    }

    public static Expression compileExpression(OpenPrimitive object, double defaultValue) {
        try {
            if (object.isNumber()) {
                return new Constant(object.doubleValue());
            }
            if (object.isString()) {
                return AnimationEngine.compile(object.stringValue());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Constant(defaultValue);
    }

    public void process(double animationTime, AnimationPlayState playState, ExecutionContext context) {
        int time = AnimationController.toTime(animationTime);
        for (var animator : animators) {
            animator.process(time, playState, context);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnimationController that)) return false;
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

    public Collection<AnimatedTransform> affectedTransforms() {
        return Collections.compactMap(animators, it -> {
            if (it instanceof Animator.Bone bone) {
                return bone.transform;
            }
            return null;
        });
    }

    public String name() {
        return name;
    }

    public SkinAnimationLoop loop() {
        return loop;
    }

    public double duration() {
        return duration;
    }

    public boolean isRequiresVirtualMachine() {
        return isRequiresVirtualMachine;
    }

    public boolean isParallel() {
        return mode != AnimatedOutputMode.MAIN;
    }

    public boolean isEmpty() {
        return animators.isEmpty();
    }

    public static AnimatedOutputMode calcMixMode(String name) {
        name = name.toLowerCase();
        if (name.matches("^(.+\\.)?pre_parallel(\\d+)$")) {
            return AnimatedOutputMode.PRE_MAIN;
        }
        if (name.matches("^(.+\\.)?parallel(\\d+)$")) {
            return AnimatedOutputMode.POST_MAIN;
        }
        return AnimatedOutputMode.MAIN;
    }

    private boolean calcRequiresVirtualMachine() {
        for (var animator : animators) {
            for (var channel : animator.channels) {
                for (var fragment : channel.fragments) {
                    if (!fragment.isConstant()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private static abstract class Selector {

        public abstract void apply(float x, float y, float z, AnimatedPoint output);

        public void apply(OpenVector3f value, AnimatedPoint snapshot) {
            apply(value.x(), value.y(), value.z(), snapshot);
        }

        public static class Translation extends Selector {

            @Override
            public void apply(float x, float y, float z, AnimatedPoint output) {
                output.setTranslation(x, y, z);
            }
        }

        public static class Rotation extends Selector {

            @Override
            public void apply(float x, float y, float z, AnimatedPoint output) {
                output.setRotation(x, y, z);
            }
        }

        public static class Scale extends Selector {

            @Override
            public void apply(float x, float y, float z, AnimatedPoint output) {
                output.setScale(x, y, z);
            }
        }
    }

    private static abstract class Animator<T> {

        protected final String name;
        protected final List<Channel<T>> channels;

        public Animator(String name, int duration, List<SkinAnimationKeyframe> linkedKeyframes) {
            var namedKeyframes = new LinkedHashMap<String, ArrayList<SkinAnimationKeyframe>>();
            for (var keyframe : linkedKeyframes) {
                namedKeyframes.computeIfAbsent(keyframe.key(), key -> new ArrayList<>()).add(keyframe);
            }
            this.name = name;
            this.channels = Collections.compactMap(namedKeyframes.entrySet(), it -> createChannel(it.getKey(), duration, it.getValue()));
        }

        public void process(int time, AnimationPlayState playState, ExecutionContext context) {
            for (var channel : channels) {
                apply(channel, time, playState, context);
            }
        }

        public abstract void apply(Channel<T> channel, int time, AnimationPlayState playState, ExecutionContext context);

        public abstract Channel<T> createChannel(String name, int duration, List<SkinAnimationKeyframe> keyframes);

        @Override
        public String toString() {
            return Objects.toString(this, "name", name);
        }

        private static class Bone extends Animator<OpenVector3f> {

            private final AnimatedOutputPoint output;
            private final AnimatedTransform transform;

            public Bone(String name, int duration, List<SkinAnimationKeyframe> linkedKeyframes, AnimatedTransform transform, AnimatedOutputMode mode) {
                super(name, duration, linkedKeyframes);
                this.output = new AnimatedOutputPoint(transform, mode);
                this.transform = transform;
                // when the output is changed, send the changes to the transform.
                if (transform != null) {
                    transform.link(output);
                }
            }

            @Override
            public void apply(Channel<OpenVector3f> channel, int time, AnimationPlayState playState, ExecutionContext context) {
                // when can't found next fragment, ignore.
                var fragment = channel.fragmentAtTime(time);
                if (fragment == null) {
                    return;
                }
                var selector = channel.selector;
                var startValue = fragment.startValue;
                var currentTime = time - fragment.startTime;
                if (currentTime <= 0) {
                    selector.apply(startValue.evaluate(context), output);
                    return;
                }
                var length = fragment.length;
                var endValue = fragment.endValue;
                if (currentTime >= length) {
                    selector.apply(endValue.evaluate(context), output);
                    return;
                }
                var function = fragment.function;
                var from = startValue.evaluate(context);
                var to = endValue.evaluate(context);
                var t = function.apply(currentTime / (float) length);
                var tx = OpenMath.lerp(t, from.x(), to.x());
                var ty = OpenMath.lerp(t, from.y(), to.y());
                var tz = OpenMath.lerp(t, from.z(), to.z());
                selector.apply(tx, ty, tz, output);
            }

            @Override
            public Channel<OpenVector3f> createChannel(String name, int duration, List<SkinAnimationKeyframe> keyframes) {
                var selector = select(name);
                if (selector != null) {
                    return new Channel.Bone(name, duration, selector, keyframes);
                }
                return null;
            }

            private Selector select(String name) {
                return switch (name) {
                    case "position" -> new Selector.Translation();
                    case "rotation" -> new Selector.Rotation();
                    case "scale" -> new Selector.Scale();
                    default -> null;
                };
            }
        }

        private static class Effect extends Animator<Object> {

            public Effect(String name, int duration, List<SkinAnimationKeyframe> linkedKeyframes) {
                super(name, duration, linkedKeyframes);
            }

            @Override
            public void apply(Channel<Object> channel, int time, AnimationPlayState playState, ExecutionContext context) {
                var fragment = channel.fragmentAtTime(time);
                var currentValue = Objects.flatMap(fragment, it -> it.startValue);
                var effectState = playState.effectByName(channel.name);
                if (effectState.value() == currentValue) {
                    return; // not any change,
                }
                if (currentValue == null) {
                    effectState.setValue(null, null);
                    return; // clean only.
                }
                var result = currentValue.evaluate(context);
                effectState.setValue(currentValue, result);
            }

            @Override
            public Channel<Object> createChannel(String name, int duration, List<SkinAnimationKeyframe> keyframes) {
                return new Channel.Effect(name, duration, null, keyframes);
            }
        }
    }

    private static abstract class Channel<T> {

        private final String name;
        private final Selector selector;
        private final List<Fragment<T>> fragments;

        private Fragment<T> current;

        public Channel(String name, int duration, Selector selector, List<SkinAnimationKeyframe> keyframes) {
            this.name = name;
            this.selector = selector;
            this.fragments = createFragments(duration, keyframes);
        }

        public abstract Pair<OptimizedExpression<T>, OptimizedExpression<T>> compile(List<SkinAnimationPoint> points);

        public Fragment<T> fragmentAtTime(int time) {
            // fast hit caching?
            if (current != null && current.contains(time)) {
                return current;
            }
            // find fragment with time.
            for (var fragment : fragments) {
                current = fragment;
                if (current.contains(time)) {
                    break;
                }
            }
            return current;
        }

        public boolean isEmpty() {
            return fragments == null || fragments.isEmpty();
        }

        @Override
        public String toString() {
            return Objects.toString(this, "name", name);
        }

        private List<Fragment<T>> createFragments(int duration, List<SkinAnimationKeyframe> keyframes) {
            var builders = new ArrayList<FragmentBuilder<T>>();
            for (var keyframe : keyframes) {
                var time = AnimationController.toTime(keyframe.time());
                var point = compile(keyframe.points());
                builders.add(new FragmentBuilder<T>(time, keyframe.function(), point.getKey(), point.getValue()));
            }
            builders.sort(Comparator.comparingInt(it -> it.leftTime));
            if (!builders.isEmpty()) {
                builders.add(0, builders.get(0).copyToBegin());
                builders.add(builders.get(builders.size() - 1).copyToEnd(duration));
            }
            // convert `L0|L0 - L0|R0 - L1|R1 - L2|R2 - R2|R2` to `|L0 - L0|R0 - L1|R1 - L2|R2 - R2|R2 - R2|`.
            for (int i = 1; i < builders.size(); i++) {
                var left = builders.get(i - 1);
                var right = builders.get(i);
                left.rightTime = right.leftTime;
                left.rightValue = right.leftValue;
                right.leftTime = right.rightTime;
                right.leftValue = right.rightValue;
            }
            // we need to remove invalid builder (e.g. zero duration),
            // but it will wrong remove all builder when total duration is zero,
            // we must keep least one builder.
            if (builders.size() > 1) {
                var first = builders.get(0);
                builders.removeIf(it -> it.leftTime == it.rightTime);
                if (builders.isEmpty()) {
                    builders.add(first);
                }
            }
            return Collections.compactMap(builders, FragmentBuilder::build);
        }

        private static class Bone extends Channel<OpenVector3f> {

            private final float defaultValue;

            public Bone(String name, int duration, Selector selector, List<SkinAnimationKeyframe> keyframes) {
                super(name, duration, selector, keyframes);
                this.defaultValue = calcDefaultValue(selector);
            }

            @Override
            public Pair<OptimizedExpression<OpenVector3f>, OptimizedExpression<OpenVector3f>> compile(List<SkinAnimationPoint> points) {
                var expressions = new ArrayList<Expression>();
                for (var point : points) {
                    if (point instanceof SkinAnimationPoint.Bone bone) {
                        expressions.add(compileExpression(bone.x(), defaultValue));
                        expressions.add(compileExpression(bone.y(), defaultValue));
                        expressions.add(compileExpression(bone.z(), defaultValue));
                    } else {
                        ModLog.warn("Not support point type: {}", point);
                        expressions.add(Constant.ZERO);
                        expressions.add(Constant.ZERO);
                        expressions.add(Constant.ZERO);
                    }
                }
                var left = OptimizedExpression.of(expressions.get(0), expressions.get(1), expressions.get(2));
                if (expressions.size() <= 3) {
                    return Pair.of(left, left);
                }
                var right = OptimizedExpression.of(expressions.get(3), expressions.get(4), expressions.get(5));
                return Pair.of(left, right);
            }

            private float calcDefaultValue(Selector selector) {
                if (selector instanceof Selector.Scale) {
                    return 1f;
                }
                return 0f;
            }
        }

        private static class Effect extends Channel<Object> {

            public Effect(String name, int duration, Selector selector, List<SkinAnimationKeyframe> keyframes) {
                super(name, duration, selector, keyframes);
            }

            @Override
            public Pair<OptimizedExpression<Object>, OptimizedExpression<Object>> compile(List<SkinAnimationPoint> points) {
                var effect = compile0(points);
                return Pair.of(effect, effect);
            }

            private OptimizedExpression<Object> compile0(List<SkinAnimationPoint> points) {
                var effects = Collections.compactMap(points, this::compile0);
                if (effects.size() == 1) {
                    return effects.get(0);
                }
                return context -> Collections.compactMap(effects, it -> it.evaluate(context));

            }

            private OptimizedExpression<Object> compile0(SkinAnimationPoint point) {
                if (point instanceof SkinAnimationPoint.Instruct instruct) {
                    return new AnimationInstructHandler(compileExpression(OpenPrimitive.of(instruct.script()), 0));
                }
                if (point instanceof SkinAnimationPoint.Sound sound) {
                    return new AnimationSoundHandler(sound);
                }
                if (point instanceof SkinAnimationPoint.Particle particle) {
                    return new AnimationParticleHandler(particle);
                }
                ModLog.warn("Not support point type: {}", point);
                return null;
            }
        }
    }

    private static class Fragment<T> {

        private final int startTime;
        private final OptimizedExpression<T> startValue;

        private final int endTime;
        private final OptimizedExpression<T> endValue;

        private final int length;
        private final SkinAnimationFunction function;

        public Fragment(int startTime, OptimizedExpression<T> startValue, int endTime, OptimizedExpression<T> endValue, SkinAnimationFunction function) {
            this.startTime = startTime;
            this.startValue = startValue;
            this.endTime = endTime;
            this.endValue = endValue;
            this.length = endTime - startTime;
            this.function = function;
        }


        public boolean contains(int time) {
            return startTime <= time && time < endTime;
        }

        public boolean isConstant() {
            return startValue.isConstant() && endValue.isConstant();
        }
    }

    private static class FragmentBuilder<T> {

        private final SkinAnimationFunction function;

        private int leftTime;
        private OptimizedExpression<T> leftValue;

        private int rightTime;
        private OptimizedExpression<T> rightValue;

        FragmentBuilder(int time, SkinAnimationFunction function, OptimizedExpression<T> leftValue, OptimizedExpression<T> rightValue) {
            this.function = function;
            this.leftTime = time;
            this.leftValue = leftValue;
            this.rightTime = time;
            this.rightValue = rightValue;
        }

        public FragmentBuilder<T> copyToBegin() {
            return new FragmentBuilder<>(0, SkinAnimationFunction.linear(), leftValue, leftValue); // head
        }

        public FragmentBuilder<T> copyToEnd(int time) {
            return new FragmentBuilder<>(time, SkinAnimationFunction.linear(), rightValue, rightValue); // tail
        }

        public Fragment<T> build() {
            return new Fragment<>(leftTime, leftValue, rightTime, rightValue, function);
        }
    }
}
