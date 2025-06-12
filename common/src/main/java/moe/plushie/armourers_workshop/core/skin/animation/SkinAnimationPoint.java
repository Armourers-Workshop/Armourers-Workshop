package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import org.jetbrains.annotations.Nullable;

public abstract class SkinAnimationPoint {

    public static class Bone extends SkinAnimationPoint {

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

    public static class Instruct extends SkinAnimationPoint {

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

    public static class Sound extends SkinAnimationPoint {

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

    public static class Particle extends SkinAnimationPoint {

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
