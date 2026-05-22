package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.particle.math.EmitterShapeDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * All particles are emitted based on a specified set of Molang expressions.
 */
public class EmitterCustomShape implements SkinParticleComponent {

    /// specifies the offset from the emitter to emit the particles
    /// evaluated once per particle emitted
    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    /// specifies the direction of particles.  Defaults to "outwards"
    /// evaluated once per particle emitted
    private final EmitterShapeDirection direction;

    public EmitterCustomShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, EmitterShapeDirection direction) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
    }

    public EmitterCustomShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        direction.writeToStream(stream);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        // TODO: NO IMPL - PARTICLE
//        var x = compiler.compile(this.x, 0.0);
//        var y = compiler.compile(this.y, 0.0);
//        var z = compiler.compile(this.z, 0.0);
//        var direction = this.direction.compile(compiler);
//        compiler.instance().prepare((emitter, particle, context) -> {
//            var cx = (float) x.compute(context);
//            var cy = (float) y.compute(context);
//            var cz = (float) z.compute(context);
//            var dir = new OpenVector3f(OpenMath.randomf() * 2 - 1, OpenMath.randomf() * 2 - 1, OpenMath.randomf() * 2 - 1);
//            dir.normalize();
//            particle.setPosition(cx + dir.x(), cy + dir.y(), cz + dir.z());
//            direction.apply(particle, cx, cy, cz, context);
//        });
    }
}
