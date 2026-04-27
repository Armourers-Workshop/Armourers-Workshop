package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SkinAnimationData {

    private final String name;

    private final Loop loop;

    private final float duration;

    private final List<Animator> animators;

    public SkinAnimationData(String name, float duration, Loop loop, List<Animator> animators) {
        this.name = name;
        this.duration = duration;
        this.loop = loop;
        this.animators = animators;
    }

    public String name() {
        return name;
    }

    public Loop loop() {
        return loop;
    }

    public float duration() {
        return duration;
    }

    public List<Animator> animators() {
        return animators;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "duration", duration, "loop", loop, "animators", animators);
    }

    public static class Animator {

        private final String bone;
        private final int options;
        private final List<Keyframe> keyframes;

        public Animator(String bone, int options, List<Keyframe> keyframes) {
            this.bone = bone;
            this.options = options;
            this.keyframes = keyframes;
        }

        public String bone() {
            return bone;
        }

        public int options() {
            return options;
        }

        public List<Keyframe> keyframes() {
            return keyframes;
        }

        @Override
        public String toString() {
            return Objects.toString(this, "bone", bone, "options", options, "keyframes", keyframes);
        }
    }

    public static class Keyframe {

        private final float time;

        private final String channel;
        private final Interpolation interpolation;

        private final List<Point> points;

        public Keyframe(float time, String channel, Interpolation interpolation, List<Point> points) {
            this.time = time;
            this.channel = channel;
            this.interpolation = interpolation;
            this.points = points;
        }

        public float time() {
            return time;
        }

        public String channel() {
            return channel;
        }

        public Interpolation interpolation() {
            return interpolation;
        }

        public List<Point> points() {
            return points;
        }

        @Override
        public String toString() {
            return Objects.toString(this, "channel", channel, "time", time, "interpolation", interpolation);
        }
    }

    public static abstract class Point {

        public static class Bone extends Point {

            private final OpenPrimitive x;
            private final OpenPrimitive y;
            private final OpenPrimitive z;

            public Bone(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public OpenPrimitive x() {
                return x;
            }

            public OpenPrimitive y() {
                return y;
            }

            public OpenPrimitive z() {
                return z;
            }

            @Override
            public String toString() {
                return Objects.toString(this, "x", x, "y", y, "z", z);
            }
        }

        public static class Instruct extends Point {

            private final String script;

            public Instruct(String script) {
                this.script = script;
            }

            public String script() {
                return script;
            }

            @Override
            public String toString() {
                return Objects.toString(this, "script", script);
            }
        }

        public static class Sound extends Point {

            private final String effect;
            private final SkinSoundData provider;

            public Sound(String effect, SkinSoundData provider) {
                this.effect = effect;
                this.provider = provider;
            }

            public String effect() {
                return effect;
            }

            public SkinSoundData provider() {
                return provider;
            }

            @Override
            public String toString() {
                return Objects.toString(this, "effect", effect, "sound", provider);
            }
        }

        public static class Particle extends Point {

            private final String effect;
            private final String locator;
            private final SkinParticleData provider;
            private final String script;

            public Particle(String effect, String locator, String script, SkinParticleData provider) {
                this.effect = effect;
                this.locator = locator;
                this.provider = provider;
                this.script = script;
            }

            public String effect() {
                return effect;
            }

            @Nullable
            public String locator() {
                return locator;
            }

            @Nullable
            public String script() {
                return script;
            }

            public SkinParticleData provider() {
                return provider;
            }

            @Override
            public String toString() {
                return Objects.toString(this, "effect", effect, "locator", locator, "script", script, "particle", provider);
            }
        }
    }

    public static abstract class Interpolation {

        public static Interpolation step() {
            return Step.INSTANCE;
        }

        public static Interpolation linear() {
            return Linear.INSTANCE;
        }

        public static Interpolation smooth() {
            return Smooth.INSTANCE;
        }

        public static Interpolation bezier(float[] parameters) {
            return new Bezier(parameters);
        }

        public static Interpolation bezier(List<Float> parameters) {
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

        public static Interpolation readFromStream(IInputStream stream) throws IOException {
            return switch (stream.readVarInt()) {
                case 3 -> bezier(stream.readFloatArray(12));
                case 2 -> smooth();
                case 1 -> step();
                case 0 -> linear();
                default -> linear(); // can't parse, downcast.
            };
        }

        public abstract void writeToStream(IOutputStream stream) throws IOException;

        /**
         * A stepped value based on the nearest step to the input value
         */
        public static class Step extends Interpolation {

            private static final Step INSTANCE = new Step();

            @Override
            public void writeToStream(IOutputStream stream) throws IOException {
                stream.writeVarInt(1);
            }

            @Override
            public String toString() {
                return "step";
            }
        }

        /**
         * A Linear interpolation Function. Functionally equivalent to no easing
         */
        public static class Linear extends Interpolation {

            private static final Linear INSTANCE = new Linear();

            @Override
            public void writeToStream(IOutputStream stream) throws IOException {
                stream.writeVarInt(0);
            }

            @Override
            public String toString() {
                return "linear";
            }
        }

        /**
         * A Smooth interpolation function, used to get smooth interpolated motion between keyframes
         * <p>
         *
         * <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">Bézier Curve</a><p/>
         * <a href="https://github.com/mrdoob/three.js/blob/master/src/extras/core/Interpolations.js">CatmullRi om</a>
         */
        public static class Smooth extends Interpolation {

            private static final Smooth INSTANCE = new Smooth();

            @Override
            public void writeToStream(IOutputStream stream) throws IOException {
                stream.writeVarInt(2);
            }

            @Override
            public String toString() {
                return "smooth";
            }
        }

        public static class Bezier extends Interpolation {

            private final float[] parameters;

            public Bezier(float[] parameters) {
                this.parameters = parameters;
            }

            @Override
            public void writeToStream(IOutputStream stream) throws IOException {
                stream.writeVarInt(3);
                stream.writeFloatArray(parameters);
            }

            @Override
            public String toString() {
                return String.format("bezier(%s)", Arrays.toString(parameters));
            }

            public float[] parameters() {
                return parameters;
            }
        }
    }

    public enum Loop {

        NONE,
        LOOP,
        LAST_FRAME,
    }
}
