package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
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
    public void compile(SkinParticleGenerator generator) {
        var spawnRate = generator.compile(this.spawnRate, 1.0);
        var maxParticles = generator.compile(this.maxParticles, 50.0);
        generator.emitter().render((emitter, context) -> {
            if (!emitter.isRunning()) {
                return;
            }
            // target particles = current time * rate(particles/sec)
            var rate = spawnRate.compute(context);
            var maxSize = maxParticles.compute(context);
            var targetSize = Math.ceil(rate * emitter.time());
            // create up to a specified size of particles.
            var size = emitter.spawnedParticles();
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
