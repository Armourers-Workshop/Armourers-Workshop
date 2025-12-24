package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.particle.math.EmitterShapeDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * This component spawns particles using a disc shape.
 * Particles can be spawned inside the shape, or on its outer perimeter.
 */
public class EmitterDiscShape implements SkinParticleComponent {

    /// specifies the offset from the emitter to emit the particles
    /// evaluated once per particle emitted
    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    /// disc radius
    /// evaluated once per particle emitted
    private final OpenPrimitive radius;

    /// specifies the normal of the disc plane, the disc will be perpendicular to this direction
    /// defaults to [ 0, 1, 0 ]
    private final OpenPrimitive planeNormalX;
    private final OpenPrimitive planeNormalY;
    private final OpenPrimitive planeNormalZ;

    /// specifies the direction of particles.  Defaults to "outwards"
    private final EmitterShapeDirection direction;

    /// emit only from the edge of the disc
    private final boolean surface;

    public EmitterDiscShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive radius, OpenPrimitive planeNormalX, OpenPrimitive planeNormalY, OpenPrimitive planeNormalZ, EmitterShapeDirection direction, boolean surface) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.planeNormalX = planeNormalX;
        this.planeNormalY = planeNormalY;
        this.planeNormalZ = planeNormalZ;
        this.direction = direction;
        this.surface = surface;
    }

    public EmitterDiscShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.radius = stream.readPrimitiveObject();
        this.planeNormalX = stream.readPrimitiveObject();
        this.planeNormalY = stream.readPrimitiveObject();
        this.planeNormalZ = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surface = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        stream.writePrimitiveObject(radius);
        stream.writePrimitiveObject(planeNormalX);
        stream.writePrimitiveObject(planeNormalY);
        stream.writePrimitiveObject(planeNormalZ);
        direction.writeToStream(stream);
        stream.writeBoolean(surface);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        var offsetX = generator.compile(this.x, 0.0);
        var offsetY = generator.compile(this.y, 0.0);
        var offsetZ = generator.compile(this.z, 0.0);
        var radius = generator.compile(this.radius, 0.0);
        var normalX = generator.compile(this.planeNormalX, 0.0);
        var normalY = generator.compile(this.planeNormalY, 1.0);
        var normalZ = generator.compile(this.planeNormalZ, 0.0);
        var direction = this.direction.compile(generator);
        var surface = this.surface;
        generator.instance().prepare((emitter, particle, context) -> {
            var cx = (float) offsetX.compute(context);
            var cy = (float) offsetY.compute(context);
            var cz = (float) offsetZ.compute(context);
            var nx = (float) normalX.compute(context);
            var ny = (float) normalY.compute(context);
            var nz = (float) normalZ.compute(context);
            var r = (float) radius.compute(context);

            var normal = new OpenVector3f(nx, ny, nz);
            normal.normalize();

            var pos = new OpenVector4f(OpenMath.randomf() - 0.5f, 0.0f, OpenMath.randomf() - 0.5f, 0.0f);
            pos.normalize();
            pos.transform(new OpenQuaternionf(normal.x(), normal.y(), normal.z(), 1));
            if (!surface) {
                r *= OpenMath.randomf();
            }
            pos.scale(r);

            particle.setPosition(cx + pos.x(), cy + pos.y(), cz + pos.z());
            direction.apply(particle, cx, cy, cz, context);
        });
    }
}
