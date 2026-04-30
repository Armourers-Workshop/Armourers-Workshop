package moe.plushie.armourers_workshop.gametest.utils;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationPose;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationCompiler;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationLinker;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("UnusedReturnValue")
public class TestAnimation {

    private final int id = OpenRandomSource.nextInt(TestAnimation.class);

    private final List<SkinAnimation> animations;

    public TestAnimation(List<SkinAnimation> animations) {
        this.animations = animations;
    }

    public int id() {
        return id;
    }

    public List<SkinAnimation> animations() {
        return animations;
    }

    private static SkinAnimationData.Loop toLoop(Object value) {
        if (value instanceof SkinAnimationData.Loop loop) {
            return loop;
        }
        if (value instanceof String string) {
            return switch (string.toLowerCase()) {
                case "none" -> SkinAnimationData.Loop.NONE;
                case "loop" -> SkinAnimationData.Loop.LOOP;
                case "last_frame" -> SkinAnimationData.Loop.LAST_FRAME;
                default -> throw new IllegalArgumentException();
            };
        }
        throw new IllegalArgumentException();
    }

    private static SkinAnimationData.Interpolation toInterpolation(Object value) {
        if (value instanceof SkinAnimationData.Interpolation interpolation) {
            return interpolation;
        }
        if (value instanceof String string) {
            return switch (string) {
                case "step" -> SkinAnimationData.Interpolation.step();
                case "linear" -> SkinAnimationData.Interpolation.linear();
                case "smooth" -> SkinAnimationData.Interpolation.smooth();
                default -> throw new IllegalArgumentException();
            };
        }
        throw new IllegalArgumentException();
    }

    public static class Builder {

        private final SkinAnimationCompiler compiler;
        private final SkinAnimationLinker linker;

        public Builder(SkinAnimationCompiler compiler, Map<String, SkinPartTransform> partTransforms) {
            this.compiler = compiler;
            this.linker = new SkinAnimationLinker(partTransforms);
        }

        public TestAnimation build(String name, double offset, double duration, Object loop, Consumer<Animators> factory) {
            var builder = new Animators(offset);
            factory.accept(builder);
            var data = new SkinAnimationData(name, (float) duration, toLoop(loop), builder.animators);
            var animation = compiler.compile(data);
            linker.link(animation);
            return new TestAnimation(Collections.newList(animation));
        }
    }

    public static class Animators {

        private final List<SkinAnimationData.Animator> animators = new ArrayList<>();

        private final double offset;

        private Animators(double offset) {
            this.offset = offset;
        }

        public Animators bone(String name, int options, Consumer<Channels> factory) {
            return add(name, options, factory);
        }

        public Animators effect(String name, int options, Consumer<Channels> factory) {
            return add("armourers:effects", options, factory);
        }

        private Animators add(String name, int options, Consumer<Channels> factory) {
            var builder = new Channels(offset);
            factory.accept(builder);
            animators.add(new SkinAnimationData.Animator(name, options, builder.keyframes));
            return this;
        }

    }

    public static class Channels {

        private final List<SkinAnimationData.Keyframe> keyframes = new ArrayList<>();

        private final double offset;

        private Channels(double offset) {
            this.offset = offset;
        }

        public Channels translation(double time, Object interpolation, Consumer<Points> factory) {
            return add("position", time, interpolation, factory);
        }

        public Channels rotation(double time, Object interpolation, Consumer<Points> factory) {
            return add("rotation", time, interpolation, factory);
        }

        public Channels scale(double time, Object interpolation, Consumer<Points> factory) {
            return add("scale", time, interpolation, factory);
        }

        public Channels instruct(double time, Object interpolation, Consumer<Points> factory) {

            return add("timeline", time, interpolation, factory);
        }

        public Channels sound(double time, Object interpolation, Consumer<Points> factory) {
            return add("sound", time, interpolation, factory);
        }

        public Channels particle(double time, Object interpolation, Consumer<Points> factory) {
            return add("particle", time, interpolation, factory);
        }

        private Channels add(String channel, double time, Object interpolation, Consumer<Points> factory) {
            var builder = new Points();
            factory.accept(builder);
            keyframes.add(new SkinAnimationData.Keyframe((float) (offset + time), channel, toInterpolation(interpolation), builder.points));
            return this;
        }
    }

    public static class Points {

        private final List<SkinAnimationData.Point> points = new ArrayList<>();

        private Points() {
        }

        public Points point(double x, double y, double z) {
            return add(new SkinAnimationData.Point.Bone(OpenPrimitive.of(x), OpenPrimitive.of(y), OpenPrimitive.of(z)));
        }

        public Points script(String value) {
            return add(new SkinAnimationData.Point.Instruct(value));
        }

        public Points sound(String value) {
            return add(new SkinAnimationData.Point.Sound(value, null));
        }

        public Points particle(String value) {
            return add(new SkinAnimationData.Point.Particle(value, "", "", null));
        }

        private Points add(SkinAnimationData.Point point) {
            points.add(point);
            return this;
        }
    }
}
