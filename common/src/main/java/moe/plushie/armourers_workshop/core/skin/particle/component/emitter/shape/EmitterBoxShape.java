package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.particle.math.EmitterShapeDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * All particles come out of a box of the specified size from the emitter.
 */
public class EmitterBoxShape implements SkinParticleComponent {

    /// specifies the offset from the emitter to emit the particles
    /// evaluated once per particle emitted
    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    /// box dimensions
    /// these are the half dimensions, the box is formed centered on the emitter
    /// with the box extending in the 3 principal x/y/z axes by these values
    private final OpenPrimitive width;
    private final OpenPrimitive height;
    private final OpenPrimitive depth;

    /// specifies the direction of particles.  Defaults to "outwards"
    /// evaluated once per particle emitted
    private final EmitterShapeDirection direction;

    /// emit only from the surface of the sphere
    private final boolean surface;

    public EmitterBoxShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive width, OpenPrimitive height, OpenPrimitive depth, EmitterShapeDirection direction, boolean surface) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.direction = direction;
        this.surface = surface;
    }

    public EmitterBoxShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.width = stream.readPrimitiveObject();
        this.height = stream.readPrimitiveObject();
        this.depth = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surface = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        stream.writePrimitiveObject(width);
        stream.writePrimitiveObject(height);
        stream.writePrimitiveObject(depth);
        direction.writeToStream(stream);
        stream.writeBoolean(surface);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var x = compiler.compile(this.x, 0.0);
        var y = compiler.compile(this.y, 0.0);
        var z = compiler.compile(this.z, 0.0);
        var width = compiler.compile(this.width, 0.0);
        var height = compiler.compile(this.height, 0.0);
        var depth = compiler.compile(this.depth, 0.0);
        var direction = this.direction.compile(compiler);
        var surface = this.surface;
        compiler.instance().prepare((emitter, particle, context) -> {
            var cx = (float) x.compute(context);
            var cy = (float) y.compute(context);
            var cz = (float) z.compute(context);
            var w = (float) width.compute(context);
            var h = (float) height.compute(context);
            var d = (float) depth.compute(context);

            var tx = cx + (OpenMath.randomf() * 2 - 1) * w;
            var ty = cy + (OpenMath.randomf() * 2 - 1) * h;
            var tz = cz + (OpenMath.randomf() * 2 - 1) * d;

            if (surface) {
                switch ((int) (OpenMath.randomf() * 6 * 100) % 6) {
                    case 0 -> tx = cx + w;
                    case 1 -> tx = cx - w;
                    case 2 -> ty = cy + h;
                    case 3 -> ty = cy - h;
                    case 4 -> tz = cz + d;
                    case 5 -> tz = cz - d;
                }
            }

            particle.setPosition(tx, ty, tz);
            direction.apply(particle, cx, cy, cz, context);
        });
    }
}
