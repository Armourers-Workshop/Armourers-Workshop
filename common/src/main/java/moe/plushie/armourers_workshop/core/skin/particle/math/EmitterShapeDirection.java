package moe.plushie.armourers_workshop.core.skin.particle.math;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticle;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

// inwards: particle direction towards center of sphere
// outwards: particle direction away from center of sphere
// direction: [<float/molang>, <float/molang>, <float/molang>]
public abstract class EmitterShapeDirection {

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    public abstract Compiled compile(SkinParticleGenerator generator);

    public boolean isCustom() {
        return this instanceof Custom;
    }

    /**
     * Particle direction is set to move towards the emitter
     */
    public static EmitterShapeDirection inwards() {
        return Builtin.INWARDS;
    }

    /**
     * Particle direction is set to move away from the emitter
     */
    public static EmitterShapeDirection outwards() {
        return Builtin.OUTWARDS;
    }

    /**
     * Set a custom direction vector in the direction field
     */
    public static EmitterShapeDirection custom(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z) {
        return new Custom(x, y, z);
    }

    public static EmitterShapeDirection readFromStream(IInputStream stream) throws IOException {
        return switch (stream.readByte()) {
            case 0 -> Builtin.INWARDS;
            case 1 -> Builtin.OUTWARDS;
            default -> new Custom(stream);
        };
    }

    private static class Builtin extends EmitterShapeDirection {

        private static final EmitterShapeDirection INWARDS = new Builtin(0, -1.0);
        private static final EmitterShapeDirection OUTWARDS = new Builtin(1, +1.0);

        private final int type;
        private final double factor;

        private Builtin(int type, double factor) {
            this.factor = factor;
            this.type = type;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeByte(type);
        }

        @Override
        public Compiled compile(SkinParticleGenerator generator) {
            var factor = this.factor;
            return (particle, x, y, z, context) -> {
                var speed = particle.position().copy();
                speed.subtract(x, y, z);
                if (speed.length() <= 0.0) {
                    speed.set(0, 0, 0);
                } else {
                    speed.normalize();
                    speed.scale((float) factor);
                }
                particle.setSpeed(speed);
            };
        }
    }

    private static class Custom extends EmitterShapeDirection {

        private final OpenPrimitive x;
        private final OpenPrimitive y;
        private final OpenPrimitive z;

        public Custom(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Custom(IInputStream stream) throws IOException {
            this.x = stream.readPrimitiveObject();
            this.y = stream.readPrimitiveObject();
            this.z = stream.readPrimitiveObject();
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeByte(0xff);
            stream.writePrimitiveObject(x);
            stream.writePrimitiveObject(y);
            stream.writePrimitiveObject(z);
        }

        @Override
        public Compiled compile(SkinParticleGenerator generator) {
            var speedX = generator.compile(this.x, 0.0);
            var speedY = generator.compile(this.y, 0.0);
            var speedZ = generator.compile(this.z, 0.0);
            return (particle, x, y, z, context) -> {
                var tx = (float) speedX.compute(context);
                var ty = (float) speedY.compute(context);
                var tz = (float) speedZ.compute(context);
                var speed = new OpenVector3f(tx, ty, tz);
                if (speed.length() <= 0.0) {
                    speed.set(0, 0, 0);
                } else {
                    speed.normalize();
                }
                particle.setSpeed(speed);
            };
        }
    }

    public interface Compiled {

        void apply(SkinParticle particle, float x, float y, float z, ExecutionContext context);
    }
}
