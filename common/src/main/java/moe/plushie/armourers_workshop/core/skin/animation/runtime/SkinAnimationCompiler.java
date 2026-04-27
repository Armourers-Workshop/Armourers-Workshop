package moe.plushie.armourers_workshop.core.skin.animation.runtime;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Constant;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SkinAnimationCompiler {

    protected final MolangVirtualMachine virtualMachine;

    public SkinAnimationCompiler(MolangVirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public Expression compile(String script, double defaultValue) {
        return compile(OpenPrimitive.of(script), defaultValue);
    }

    public Expression compile(OpenPrimitive object, double defaultValue) {
        try {
            if (object.isNumber()) {
                return new Constant(object.doubleValue());
            }
            if (object.isString()) {
                return virtualMachine.compile(object.stringValue());
            }
        } catch (Exception exception) {
            ModLog.error("Failed to compile expression", exception);
        }
        return new Constant(defaultValue);
    }

    public SkinAnimation compile(SkinAnimationData data) {
        var animators = new ArrayList<SkinAnimation.Animator<?>>();
        for (var animator : data.animators()) {
            var allKeyframes = new HashMap<String, ArrayList<SkinAnimationData.Keyframe>>();
            animator.keyframes().forEach(it -> allKeyframes.computeIfAbsent(it.channel(), key -> new ArrayList<>()).add(it));
            for (var channel : SkinAnimation.Channel.values()) {
                var keyframes = allKeyframes.get(channel.key());
                if (keyframes != null) {
                    animators.add(compile(data, animator.bone(), channel, keyframes));
                }
            }
        }
        animators.removeIf(SkinAnimation.Animator::isEmpty);
        return new SkinAnimation(data.name(), data.duration(), data.loop(), animators);
    }

    protected OptimizedExpression<?> compile(SkinAnimationData data, SkinAnimationData.Point point) {
        ModLog.warn("Not support point type: {}", point);
        return null;
    }

    protected OptimizedExpression<?> compile(SkinAnimationData data, SkinAnimationData.Point.Bone point, OpenVector3f defaultValue) {
        var x = compile(point.x(), defaultValue.x());
        var y = compile(point.y(), defaultValue.y());
        var z = compile(point.z(), defaultValue.z());
        return OptimizedExpression.of(x, y, z);
    }

    private <T> SkinAnimation.Animator<T> compile(SkinAnimationData data, String name, SkinAnimation.Channel channel, List<SkinAnimationData.Keyframe> keyframes) {
        var fragments = new ArrayList<Fragment<T>>();
        for (var keyframe : keyframes) {
            fragments.add(compile(data, name, channel, keyframe));
        }
        fragments.removeIf(Objects::isNull);
        fragments.sort(Comparator.comparingInt(it -> it.fromTime));
        if (!fragments.isEmpty()) {
            fragments.add(0, fragments.get(0).copyToBegin());
            fragments.add(fragments.get(fragments.size() - 1).copyToEnd(SkinAnimation.toTime(data.duration())));
        }
        // convert `L0|L0 - L0|R0 - L1|R1 - L2|R2 - R2|R2` to `|L0 - L0|R0 - L1|R1 - L2|R2 - R2|R2 - R2|`.
        for (int i = 1; i < fragments.size(); i++) {
            var left = fragments.get(i - 1);
            var right = fragments.get(i);
            left.toTime = right.fromTime;
            left.toValue = right.fromValue;
            left.toInterpolation = right.fromInterpolation;
            right.fromTime = right.toTime;
            right.fromValue = right.toValue;
            right.fromInterpolation = right.toInterpolation;
        }
        // we need to remove invalid builder (e.g. zero duration),
        // but it will wrong remove all builder when total duration is zero,
        // we must keep least one builder.
        if (fragments.size() > 1) {
            var first = fragments.get(0);
            fragments.removeIf(it -> it.fromTime == it.toTime);
            if (fragments.isEmpty()) {
                fragments.add(first);
            }
        }
        // set up the previous/next frame value.
        for (int i = 0; i < fragments.size(); i++) {
            var previousIndex = i - 1;
            var nextIndex = i + 1;
            if (data.loop() == SkinAnimationData.Loop.LOOP) {
                if (previousIndex < 0) {
                    previousIndex = fragments.size() - 1;
                }
                if (nextIndex >= fragments.size()) {
                    nextIndex = 0;
                }
            }
            var current = fragments.get(i);
            if (previousIndex >= 0) {
                current.previousValue = fragments.get(previousIndex).fromValue;
            }
            if (nextIndex < fragments.size()) {
                current.nextValue = fragments.get(nextIndex).toValue;
            }
        }
        return new SkinAnimation.Animator<>(name, channel, Collections.compactMap(fragments, Fragment::build));
    }

    private <T> Fragment<T> compile(SkinAnimationData data, String name, SkinAnimation.Channel channel, SkinAnimationData.Keyframe keyframe) {
        var time = SkinAnimation.toTime(keyframe.time());
        var interpolation = keyframe.interpolation();
        var points = Collections.compactMap(keyframe.points(), it -> {
            // ..
            if (it instanceof SkinAnimationData.Point.Bone point) {
                var defaultValue = OpenVector3f.ZERO;
                if (channel == SkinAnimation.Channel.SCALE) {
                    defaultValue = OpenVector3f.ONE;
                }
                return compile(data, point, defaultValue);
            }
            return compile(data, it);
        });
        if (points.size() >= 2) {
            return new Fragment<>(channel, time, Objects.unsafeCast(points.get(0)), Objects.unsafeCast(points.get(1)), interpolation);
        }
        if (points.size() == 1) {
            return new Fragment<>(channel, time, Objects.unsafeCast(points.get(0)), Objects.unsafeCast(points.get(0)), interpolation);
        }
        return null;
    }

    /**
     * Mutable compile-time keyframe fragment used to stitch neighboring segments.
     */
    private static class Fragment<T> {

        private final SkinAnimation.Channel channel;

        private int fromTime;
        private int toTime;

        private OptimizedExpression<T> fromValue;
        private OptimizedExpression<T> toValue;

        private OptimizedExpression<T> previousValue;
        private OptimizedExpression<T> nextValue;

        private SkinAnimationData.Interpolation fromInterpolation;
        private SkinAnimationData.Interpolation toInterpolation;

        Fragment(SkinAnimation.Channel channel, int time, OptimizedExpression<T> fromValue, OptimizedExpression<T> toValue, SkinAnimationData.Interpolation interpolation) {
            this.channel = channel;
            this.fromTime = time;
            this.fromValue = fromValue;
            this.toTime = time;
            this.toValue = toValue;
            this.fromInterpolation = interpolation;
            this.toInterpolation = interpolation;
        }

        public Fragment<T> copyToBegin() {
            return new Fragment<>(channel, 0, fromValue, fromValue, SkinAnimationData.Interpolation.linear()); // head
        }

        public Fragment<T> copyToEnd(int time) {
            return new Fragment<>(channel, time, toValue, toValue, SkinAnimationData.Interpolation.linear()); // tail
        }

        public SkinAnimation.Keyframe<T> build() {
            return new SkinAnimation.Keyframe<>(fromTime, toTime, previousValue, fromValue, toValue, nextValue, Objects.unsafeCast(createInterpolator()));
        }

        private SkinAnimation.Interpolator<?> createInterpolator() {
            // the effect keyframe can't support interpolation functions.
            if (channel.isEffect()) {
                return SkinAnimation.Interpolator.none();
            }
            var duration = Math.max(toTime - fromTime, 1) / 1000f;
            var from = fromInterpolation;
            var to = toInterpolation;
            if (from instanceof SkinAnimationData.Interpolation.Step) {
                return SkinAnimation.Interpolator.step();
            }
            if (from instanceof SkinAnimationData.Interpolation.Linear && (to instanceof SkinAnimationData.Interpolation.Linear || to instanceof SkinAnimationData.Interpolation.Step)) {
                return SkinAnimation.Interpolator.linear();
            }
            if (from instanceof SkinAnimationData.Interpolation.Smooth || to instanceof SkinAnimationData.Interpolation.Smooth) {
                return SkinAnimation.Interpolator.smooth();
            }
            if (from instanceof SkinAnimationData.Interpolation.Bezier || to instanceof SkinAnimationData.Interpolation.Bezier) {
                var lhs = getBezierParameters(from);
                var rhs = getBezierParameters(to);
                var t0 = new OpenVector3f(lhs[6] / duration, lhs[7] / duration, lhs[8] / duration);
                var t1 = new OpenVector3f(rhs[0] / duration, rhs[1] / duration, rhs[2] / duration);
                var p0 = new OpenVector3f(lhs[9], lhs[10], lhs[11]);
                var p1 = new OpenVector3f(rhs[3], rhs[4], rhs[5]);
                return SkinAnimation.Interpolator.bezier(t0.adding(0, 0, 0), p0, t1.adding(1, 1, 1), p1);
            }
            return SkinAnimation.Interpolator.linear();
        }

        protected static float[] getBezierParameters(SkinAnimationData.Interpolation interpolation) {
            if (interpolation instanceof SkinAnimationData.Interpolation.Bezier bezier) {
                return bezier.parameters();
            }
            return new float[]{-0.1f, -0.1f, -0.1f, 0.0f, 0.0f, 0.0f, 0.1f, 0.1f, 0.1f, 0.0f, 0.0f, 0.0f};
        }
    }
}
