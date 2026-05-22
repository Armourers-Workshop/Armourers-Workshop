package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.math.EmitterShapeDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * All particles come out of the axis-aligned bounding box (AABB) for the entity the emitter is attached to, or the emitter point if no entity.
 */
public class EmitterEntityShape implements SkinParticleComponent {

    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    /// evaluated once per particle emitted
    /// defaults to outwards
    private final EmitterShapeDirection direction;

    /// emit only from the surface of the sphere
    private final boolean surface;

    public EmitterEntityShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, EmitterShapeDirection direction, boolean surfaceOnly) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.surface = surfaceOnly;
    }

    public EmitterEntityShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surface = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        direction.writeToStream(stream);
        stream.writeBoolean(surface);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var x = compiler.compile(this.x, 0.0);
        var y = compiler.compile(this.y, 0.0);
        var z = compiler.compile(this.z, 0.0);
        var direction = this.direction.compile(compiler);
        var surface = this.surface;
        compiler.instance().prepare((emitter, particle, context) -> {
            var cx = (float) x.compute(context);
            var cy = (float) y.compute(context);
            var cz = (float) z.compute(context);

            var size = emitter.size();

            var width = size.x();
            var height = size.y();
            var depth = size.z();

            var tx = cx + (OpenMath.randomf() - 0.5f) * width;
            var ty = cy + (OpenMath.randomf() - 0.5f) * height;
            var tz = cz + (OpenMath.randomf() - 0.5f) * depth;

            if (surface) {
                switch ((int) (OpenMath.randomf() * 6 * 100) % 6) {
                    case 0 -> tx = cx + width / 2;
                    case 1 -> tx = cx - width / 2;
                    case 2 -> ty = cy + height / 2;
                    case 3 -> ty = cy - height / 2;
                    case 4 -> tz = cz + depth / 2;
                    case 5 -> tz = cz - depth / 2;
                }
            }

            particle.setPosition(tx, ty, tz);
            direction.apply(particle, cx, cy, cz, context);
        });
    }
}
