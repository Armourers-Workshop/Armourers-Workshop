package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.particle.math.EmitterShapeDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * All particles come out of a point offset from the emitter.
 */
public class EmitterPointShape implements SkinParticleComponent {

    /// specifies the offset from the emitter to emit the particles
    /// evaluated once per particle emitted
    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    /// specifies the direction of particles.
    /// evaluated once per particle emitted.
    private final EmitterShapeDirection direction;

    public EmitterPointShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, EmitterShapeDirection direction) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
    }

    public EmitterPointShape(IInputStream stream) throws IOException {
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
        var x = compiler.compile(this.x, 0.0);
        var y = compiler.compile(this.y, 0.0);
        var z = compiler.compile(this.z, 0.0);
        var direction = this.direction.compile(compiler);
        var custom = this.direction.isCustom();
        compiler.instance().prepare((emitter, particle, context) -> {
            var tx = (float) x.compute(context);
            var ty = (float) y.compute(context);
            var tz = (float) z.compute(context);
            particle.setPosition(tx, ty, tz);
            if (custom) {
                direction.apply(particle, tx, ty, tz, context);
            }
        });
    }
}
