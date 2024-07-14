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
    private final DisplayMode displayMode;

    private final ArrayList<Section> sections = new ArrayList<>();

    public AnimationController(SkinAnimation animation, Map<String, BakedSkinPart> bones) {
        this.name = animation.getName();
        this.animation = animation;

        this.loop = animation.getLoop();
        this.duration = animation.getDuration();
        this.displayMode = DisplayMode.of(animation.getName());

        // create all animation.
        animation.getValues().forEach((boneName, linkedValues) -> {
            var bone = bones.get(boneName);
            if (bone != null) {
                sections.add(new Section(bone, AnimationController.toTime(duration), linkedValues));
            }
        });
    }

    public static int toTime(float time) {
        return (int) (time * 1000);
    }

    public void process(float animationTicks) {
        int time = AnimationController.toTime(animationTicks);
        for (var section : sections) {
            var snapshot = section.snapshot;
            for (var channel : section.channels) {
                var fragment = channel.getFragmentAtTime(time);
                if (fragment != null) {
                    fragment.apply(channel.selector, snapshot, time - fragment.startTime);
                }
            }
        }
    }

    public void reset() {
        for (var section : sections) {
            section.snapshot.reset();
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
        for (var section : sections) {
            for (var channel : section.channels) {
                for (var fragment : channel.fragments) {
                    if (!fragment.startValue.isConstant() || !fragment.endValue.isConstant()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isParallel() {
        return displayMode.isParallel();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public static class Section {

        private final BakedSkinPart bone;
        private final List<Channel> channels;

        private final Snapshot snapshot;

        public Section(BakedSkinPart bone, int duration, List<SkinAnimationValue> linkedValues) {
            this.channels = create(duration, linkedValues);
            this.bone = bone;
            this.snapshot = linkTo(bone);
        }

        private List<Channel> create(float duration, List<SkinAnimationValue> linkedValues) {
            var namedValues = new LinkedHashMap<String, ArrayList<SkinAnimationValue>>();
            for (var value : linkedValues) {
                namedValues.computeIfAbsent(value.getKey(), key -> new ArrayList<>()).add(value);
            }
            return namedValues.entrySet().stream().map(it -> new Channel(it.getKey(), duration, it.getValue())).toList();
        }

        private Snapshot linkTo(BakedSkinPart bone) {
            // when animation transform already been created, we just link it directly.
            for (var transform : bone.getTransform().getChildren()) {
                if (transform instanceof AnimationTransform animatedTransform) {
                    return new Snapshot(animatedTransform);
                }
            }
            // if part have a non-standard transform (preview mode),
            // we wil think this part can't be support animation.
            if (!(bone.getPart().getTransform() instanceof SkinTransform parent)) {
                return new Snapshot(null);
            }
            // we will replace the standard transform to animated transform.
            var animatedTransform = new AnimationTransform(parent);
            bone.getTransform().replaceChild(parent, animatedTransform);
            return new Snapshot(animatedTransform);
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
            return builders.stream().map(FragmentBuilder::build).toList();
        }

        private Pair<Point, Point> compile(List<Object> points, float defaultValue) {
            var providers = new Expression[6];
            for (int i = 0; i < providers.length; ++i) {
                if (i < points.size()) {
                    providers[i] = compile(points[i], defaultValue);
                } else {
                    providers[i] = providers[i % points.size()];
                }
            }
            var left = new Point(providers[0], providers[1], providers[2]);
            var right = new Point(providers[3], providers[4], providers[5]);
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
        private final Point startValue;

        private final int endTime;
        private final Point endValue;

        private final int length;
        private final SkinAnimationFunction function;

        public Fragment(int startTime, Point startValue, int endTime, Point endValue, SkinAnimationFunction function) {
            this.startTime = startTime;
            this.startValue = startValue;
            this.endTime = endTime;
            this.endValue = endValue;
            this.length = endTime - startTime;
            this.function = function;
        }

        public void apply(Selector selector, Snapshot snapshot, int time) {
            if (time <= 0) {
                selector.apply(snapshot, startValue.get());
            } else if (time >= length) {
                selector.apply(snapshot, endValue.get());
            } else {
                var from = startValue.get();
                var to = endValue.get();
                var t = function.apply(time / (float) length);
                var tx = lerp(from.getX(), to.getX(), t);
                var ty = lerp(from.getY(), to.getY(), t);
                var tz = lerp(from.getZ(), to.getZ(), t);
                selector.apply(snapshot, tx, ty, tz);
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
        private final Point leftValue;

        private int rightTime;
        private Point rightValue;

        FragmentBuilder(int time, SkinAnimationFunction function, Point leftValue, Point rightValue) {
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

    public static abstract class Selector {

        public static Selector of(String channel) {
            return switch (channel) {
                case "position" -> new Translation();
                case "rotation" -> new Rotation();
                case "scale" -> new Scale();
                default -> new None();
            };
        }

        protected abstract void apply(Snapshot snapshot, float x, float y, float z);

        protected void apply(Snapshot snapshot, Vector3f value) {
            apply(snapshot, value.getX(), value.getY(), value.getZ());
        }

        public static class None extends Selector {

            @Override
            protected void apply(Snapshot snapshot, float x, float y, float z) {
                // none
            }
        }

        public static class Translation extends Selector {

            @Override
            protected void apply(Snapshot snapshot, float x, float y, float z) {
                snapshot.setTranslate(x, y, z);
            }
        }

        public static class Rotation extends Selector {

            @Override
            protected void apply(Snapshot snapshot, float x, float y, float z) {
                snapshot.setRotate(x, y, z);
            }
        }

        public static class Scale extends Selector {

            @Override
            protected void apply(Snapshot snapshot, float x, float y, float z) {
                snapshot.setScale(x, y, z);
            }
        }
    }

    public static class Snapshot {

        protected int flags = 0x00;

        protected final Vector3f translate = new Vector3f();
        protected final Vector3f rotation = new Vector3f();
        protected final Vector3f scale = new Vector3f(1, 1, 1);

        private final AnimationTransform transform;

        public Snapshot(AnimationTransform transform) {
            this.transform = transform;
            if (transform != null) {
                transform.link(this);
            }
        }

        public void setTranslate(float x, float y, float z) {
            translate.set(x, y, z);
            mark(0x10);
        }

        public Vector3f getTranslate() {
            if ((flags & 0x10) != 0) {
                return translate;
            }
            return Vector3f.ZERO;
        }

        public void setRotate(float x, float y, float z) {
            rotation.set(x, y, z);
            mark(0x20);
        }

        public Vector3f getRotation() {
            if ((flags & 0x20) != 0) {
                return rotation;
            }
            return Vector3f.ZERO;
        }

        public void setScale(float x, float y, float z) {
            scale.set(x, y, z);
            mark(0x40);
        }

        public Vector3f getScale() {
            if ((flags & 0x40) != 0) {
                return scale;
            }
            return Vector3f.ONE;
        }

        public void reset() {
            mark(flags);
            flags = 0x00;
        }

        public void mark(int newFlags) {
            flags |= newFlags;
            if (transform != null) {
                transform.mark(newFlags);
            }
        }
    }

    public static class Point {

        private final Runnable updater;
        private final Vector3f variable = new Vector3f();

        public Point(Expression x, Expression y, Expression z) {
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

    public enum DisplayMode {

        SERIAL, PARALLEL;

        public static DisplayMode of(String name) {
            if (name != null && name.matches("^(.+\\.)?parallel\\d+$")) {
                return PARALLEL;
            }
            return SERIAL;
        }


        public boolean isParallel() {
            return this == PARALLEL;
        }
    }
}
