package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * All particles are spawned instantly at the start of the emitter's lifetime
 */
public class EmitterInstantRate implements SkinParticleComponent {

    /// this many particles are emitted at once,
    /// evaluated once per particle emitter loop.
    private final OpenPrimitive particles;

    public EmitterInstantRate(OpenPrimitive particles) {
        this.particles = particles;
    }

    public EmitterInstantRate(IInputStream stream) throws IOException {
        this.particles = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(particles);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var particles = compiler.compile(this.particles, 10.0);
        compiler.emitter().tick((emitter, context) -> {
            int count = particles.evaluate(context).intValue();
            var time = emitter.time();
            if (!emitter.isRunning() || Double.compare(time, 0.0) == 0) {
                return;
            }
            for (int i = 0; i < count; i++) {
                emitter.spawn();
            }
        });
    }
}
