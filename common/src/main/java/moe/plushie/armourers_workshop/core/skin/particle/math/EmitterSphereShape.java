package moe.plushie.armourers_workshop.core.skin.particle.math;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * All particles come out of a sphere offset from the emitter.
 */
public class EmitterSphereShape implements SkinParticleComponent {

    /// specifies the offset from the emitter to emit the particles
    /// evaluated once per particle emitted
    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    /// sphere radius
    /// evaluated once per particle emitted
    private final OpenPrimitive radius;

    /// specifies the direction of particles.  Defaults to "outwards"
    /// evaluated once per particle emitted
    private final EmitterShapeDirection direction;

    /// emit only from the surface of the sphere
    private final boolean surface;

    public EmitterSphereShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive radius, EmitterShapeDirection direction, boolean surface) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.direction = direction;
        this.surface = surface;
    }

    public EmitterSphereShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.radius = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surface = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        stream.writePrimitiveObject(radius);
        direction.writeToStream(stream);
        stream.writeBoolean(surface);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        var x = generator.compile(this.x, 0.0);
        var y = generator.compile(this.y, 0.0);
        var z = generator.compile(this.z, 0.0);
        var radius = generator.compile(this.radius, 0.0);
        var direction = this.direction.compile(generator);
        var surface = this.surface;
        generator.instance().prepare((emitter, particle, context) -> {
            var cx = (float) x.compute(context);
            var cy = (float) y.compute(context);
            var cz = (float) z.compute(context);
            var r = (float) radius.compute(context);
            var dir = new OpenVector3f(OpenMath.randomf() * 2 - 1, OpenMath.randomf() * 2 - 1, OpenMath.randomf() * 2 - 1);
            dir.normalize();
            if (!surface) {
                r *= OpenMath.randomf();
            }
            dir.scale(r);
            particle.setPosition(cx + dir.x(), cy + dir.y(), cz + dir.z());
            direction.apply(particle, cx, cy, cz, context);
        });
    }
}
