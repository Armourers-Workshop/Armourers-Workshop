package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Particles come out at a steady or Molang rate over time.
 */
public class EmitterSteadyRate implements SkinParticleComponent {

    /// how often a particle is emitted, in particles/sec
    /// evaluated once per particle emitted
    private final OpenPrimitive spawnRate;

    /// maximum number of particles that can be active at once for this emitter
    /// evaluated once per particle emitter loop
    private final OpenPrimitive maxParticles;

    public EmitterSteadyRate(OpenPrimitive spawnRate, OpenPrimitive maxParticles) {
        this.spawnRate = spawnRate;
        this.maxParticles = maxParticles;
    }

    public EmitterSteadyRate(IInputStream stream) throws IOException {
        this.spawnRate = stream.readPrimitiveObject();
        this.maxParticles = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(spawnRate);
        stream.writePrimitiveObject(maxParticles);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var spawnRate = compiler.compile(this.spawnRate, 1.0);
        var maxParticles = compiler.compile(this.maxParticles, 50.0);
        compiler.emitter().render((emitter, context) -> {
            if (!emitter.isRunning()) {
                return;
            }
            var rate = spawnRate.compute(context);
            var maxSize = maxParticles.compute(context);
            // create up to a specified size of particles.
            var size = emitter.particles().size();
            var targetSize = size + rate * emitter.deltaTime();
            for (var i = size; i < targetSize && i < maxSize; i++) {
                emitter.spawn();
            }
        });
    }

    @Override
    public int priority() {
        return 10;
    }
}
