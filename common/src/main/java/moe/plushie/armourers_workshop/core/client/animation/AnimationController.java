package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationValue;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnimationController {

    private final int id = ThreadUtils.nextId(AnimationController.class);

    private final String name;
    private final SkinAnimation animation;

    private final float duration;
    private final SkinAnimationLoop loop;

    private final ArrayList<Bone> bones = new ArrayList<>();

    private final boolean isParallel;
    private final boolean isRequiresVirtualMachine;

    public AnimationController(SkinAnimation animation, Map<String, BakedSkinPart> bones) {
        this.name = animation.getName();
        this.animation = animation;

        this.loop = animation.getLoop();
        this.duration = animation.getDuration();

        // create all animation.
        animation.getValues().forEach((boneName, linkedValues) -> {
            var bone = bones.get(boneName);
            if (bone != null) {
                this.bones.add(new Bone(bone, AnimationController.toTime(duration), linkedValues));
            }
        });

        this.isParallel = calcParallel();
        this.isRequiresVirtualMachine = calcRequiresVirtualMachine();
    }

    public static int toTime(float time) {
        return (int) (time * 1000);
    }

    public void process(float animationTicks) {
        int time = AnimationController.toTime(animationTicks);
        for (var bone : bones) {
            for (var channel : bone.channels) {
                var fragment = channel.getFragmentAtTime(time);
                if (fragment != null) {
                    fragment.apply(channel.selector, bone.output, time - fragment.startTime);
                }
            }
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
        return ObjectUtils.makeDescription(this, "name", name, "duration", duration, "loop", loop);
    }

    public String getName() {
        return name;
    }

    public SkinAnimationLoop getLoop() {
        return loop;
    }

    public float getDuration() {
        return duration;
    }

    public boolean isRequiresVirtualMachine() {
        return isRequiresVirtualMachine;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public boolean isEmpty() {
        return bones.isEmpty();
    }

    private boolean calcParallel() {
        return name != null && name.matches("^(.+\\.)?parallel\\d+$");
    }

    private boolean calcRequiresVirtualMachine() {
        for (var bone : bones) {
            for (var channel : bone.channels) {
                for (var fragment : channel.fragments) {
                    if (!fragment.startValue.isConstant() || !fragment.endValue.isConstant()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static class Bone {

        private final BakedSkinPart part;
        private final List<Channel> channels;

        private final Output output;

        public Bone(BakedSkinPart part, int duration, List<SkinAnimationValue> linkedValues) {
            this.channels = create(duration, linkedValues);
            this.part = part;
            this.output = linkTo(part);
        }

        private List<Channel> create(float duration, List<SkinAnimationValue> linkedValues) {
            var namedValues = new LinkedHashMap<String, ArrayList<SkinAnimationValue>>();
            for (var value : linkedValues) {
                namedValues.computeIfAbsent(value.getKey(), key -> new ArrayList<>()).add(value);
            }
            return ObjectUtils.map(namedValues.entrySet(), it -> new Channel(it.getKey(), duration, it.getValue()));
        }

        private Output linkTo(BakedSkinPart bone) {
            // when animation transform already been created, we just link it directly.
            for (var transform : bone.getTransform().getChildren()) {
                if (transform instanceof AnimatedTransform animatedTransform) {
                    return new Output(animatedTransform);
                }
            }
            // if part have a non-standard transform (preview mode),
            // we wil think this part can't be support animation.
            if (!(bone.getPart().getTransform() instanceof SkinTransform parent)) {
                return new Output(null);
            }
            // we will replace the standard transform to animated transform.
            var animatedTransform = new AnimatedTransform(parent);
            bone.getTransform().replaceChild(parent, animatedTransform);
            return new Output(animatedTransform);
        }
    }

    public static class Channel {

        private final Selector selector;
        private final Fragment[] fragments;

        private Fragment current;

        public Channel(String name, float duration, List<SkinAnimationValue> values) {
            this.selector = Selector.of(name);
            this.fragments = create(duration, values).toArray(new Fragment[0]);
        }

        public Fragment getFragmentAtTime(int time) {
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
            return fragments == null || fragments.length == 0;
        }

        private List<Fragment> create(float duration, List<SkinAnimationValue> values) {
            var defaultValue = calcDefaultValue();
            var builders = new ArrayList<FragmentBuilder>();
            for (var value : values) {
                var time = AnimationController.toTime(value.getTime());
                var point = compile(value.getPoints(), defaultValue);
                builders.add(new FragmentBuilder(time, value.getFunction(), point.getLeft(), point.getRight()));
            }
            builders.sort(Comparator.comparingInt(it -> it.leftTime));
            if (!builders.isEmpty()) {
                builders.add(0, builders.get(0).copy(AnimationController.toTime(0)));
                builders.add(builders.get(builders.size() - 1).copy(AnimationController.toTime(duration)));
            }
            for (int i = 1; i < builders.size(); i++) {
                var left = builders.get(i - 1);
                var right = builders.get(i);
                left.rightTime = right.leftTime;
                left.rightValue = right.leftValue;
            }
            builders.removeIf(it -> it.leftTime == it.rightTime);
            return ObjectUtils.map(builders, FragmentBuilder::build);
        }

        private Pair<Value, Value> compile(List<Object> points, float defaultValue) {
            var expressions = new Expression[6];
            for (int i = 0; i < expressions.length; ++i) {
                if (i < points.size()) {
                    expressions[i] = compile(points[i], defaultValue);
                } else {
                    expressions[i] = expressions[i % points.size()];
                }
            }
            var left = new Value(expressions[0], expressions[1], expressions[2]);
            var right = new Value(expressions[3], expressions[4], expressions[5]);
            return Pair.of(left, right);
        }

        private Expression compile(Object object, double defaultValue) {
            try {
                if (object instanceof Number number) {
                    return new Constant(number.doubleValue());
                }
                if (object instanceof String script) {
                    return MolangVirtualMachine.get().eval(script);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return new Constant(defaultValue);
        }

        private float calcDefaultValue() {
            if (selector instanceof Selector.Scale) {
                return 1f;
            }
            return 0f;
        }
    }

    public static class Fragment {

        private final int startTime;
        private final Value startValue;

        private final int endTime;
        private final Value endValue;

        private final int length;
        private final SkinAnimationFunction function;

        public Fragment(int startTime, Value startValue, int endTime, Value endValue, SkinAnimationFunction function) {
            this.startTime = startTime;
            this.startValue = startValue;
            this.endTime = endTime;
            this.endValue = endValue;
            this.length = endTime - startTime;
            this.function = function;
        }

        public void apply(Selector selector, Output output, int time) {
            if (time <= 0) {
                selector.apply(output, startValue.get());
            } else if (time >= length) {
                selector.apply(output, endValue.get());
            } else {
                var from = startValue.get();
                var to = endValue.get();
                var t = function.apply(time / (float) length);
                var tx = lerp(from.getX(), to.getX(), t);
                var ty = lerp(from.getY(), to.getY(), t);
                var tz = lerp(from.getZ(), to.getZ(), t);
                selector.apply(output, tx, ty, tz);
            }
        }

        public float lerp(float start, float end, float t) {
            return start + (end - start) * t;
        }

        public boolean contains(int time) {
            return startTime <= time && time < endTime;
        }
    }

    public static class FragmentBuilder {

        private final SkinAnimationFunction function;

        private final int leftTime;
        private final Value leftValue;

        private int rightTime;
        private Value rightValue;

        FragmentBuilder(int time, SkinAnimationFunction function, Value leftValue, Value rightValue) {
            this.function = function;
            this.leftTime = time;
            this.leftValue = leftValue;
            this.rightTime = time;
            this.rightValue = rightValue;
        }

        public FragmentBuilder copy(int time) {
            if (time > 0) {
                return new FragmentBuilder(time, SkinAnimationFunction.linear(), rightValue, rightValue); // tail
            } else {
                return new FragmentBuilder(time, SkinAnimationFunction.linear(), leftValue, leftValue); // head
            }
        }

        public Fragment build() {
            return new Fragment(leftTime, leftValue, rightTime, rightValue, function);
        }
    }

    public static class Selector {

        public static Selector of(String channel) {
            return switch (channel) {
                case "position" -> new Translation();
                case "rotation" -> new Rotation();
                case "scale" -> new Scale();
                default -> new Selector();
            };
        }

        protected void apply(Output output, float x, float y, float z) {
        }

        protected void apply(Output snapshot, Vector3f value) {
            apply(snapshot, value.getX(), value.getY(), value.getZ());
        }

        public static class Translation extends Selector {

            @Override
            protected void apply(Output output, float x, float y, float z) {
                output.setTranslate(x, y, z);
            }
        }

        public static class Rotation extends Selector {

            @Override
            protected void apply(Output output, float x, float y, float z) {
                output.setRotate(x, y, z);
            }
        }

        public static class Scale extends Selector {

            @Override
            protected void apply(Output output, float x, float y, float z) {
                output.setScale(x, y, z);
            }
        }
    }

    public static class Output extends AnimatedPoint {

        private final AnimatedTransform transform;

        public Output(AnimatedTransform transform) {
            this.transform = transform;
            if (transform != null) {
                transform.link(this);
            }
        }

        @Override
        public void setDirty(int newFlags) {
            super.setDirty(newFlags);
            if (transform != null) {
                transform.setDirty(newFlags);
            }
        }
    }

    public static class Value {

        private final Runnable updater;
        private final Vector3f variable = new Vector3f();

        public Value(Expression x, Expression y, Expression z) {
            this.updater = build(x, y, z);
        }

        public Vector3f get() {
            if (updater != null) {
                updater.run();
            }
            return variable;
        }

        public boolean isConstant() {
            return updater == null;
        }

        private Runnable build(Expression x, Expression y, Expression z) {
            // something requires to be calculated.
            if (x.isMutable() || y.isMutable() || z.isMutable()) {
                return () -> variable.set(x.getAsFloat(), y.getAsFloat(), z.getAsFloat());
            }
            // all is constant.
            variable.set(x.getAsFloat(), y.getAsFloat(), z.getAsFloat());
            return null;
        }
    }

    public static class PlayState {

        private float startTime0;
        private final float startTime;
        private final float duration;

        private int playCount;

        private float adjustedTicks = 0;

        public PlayState(AnimationController animationController, float atTime, int playCount) {
            this.duration = animationController.getDuration();
            this.startTime = atTime;
            this.startTime0 = atTime;
            this.playCount = calcPlayCount(playCount, animationController.getLoop());
        }

        public void tick(float animationTicks) {
            adjustedTicks = animationTicks - startTime0;
            if (playCount == 0 || adjustedTicks < duration) {
                return;
            }
            // 0 -> duration / 0 -> duration ...
            if (playCount > 0) {
                playCount -= 1;
            }
            if (playCount != 0) { // reset
                adjustedTicks -= duration;
                startTime0 = animationTicks - adjustedTicks;
            }
        }


        public float getStartTicks() {
            return startTime;
        }

        public float getAdjustedTicks(float animationTicks) {
            tick(animationTicks);
            return adjustedTicks;
        }

        public int getPlayCount() {
            return playCount;
        }

        public boolean isCompleted() {
            return playCount == 0 && adjustedTicks > duration;
        }

        private int calcPlayCount(int playCount, SkinAnimationLoop loop) {
            if (playCount == 0) {
                return switch (loop) {
                    case NONE -> 1;
                    case LAST_FRAME -> 0;
                    case LOOP -> -1;
                };
            }
            return playCount;
        }
    }
}
