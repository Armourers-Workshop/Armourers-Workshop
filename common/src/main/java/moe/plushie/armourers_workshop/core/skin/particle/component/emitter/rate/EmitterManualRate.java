package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Particle emission will occur only when the emitter is told to emit via the game itself. This is mostly used by legacy particle effects.
 */
public class EmitterManualRate implements SkinParticleComponent {

    /// Evaluated once per particle emitted
    private final OpenPrimitive maxParticles;

    public EmitterManualRate(OpenPrimitive maxParticles) {
        this.maxParticles = maxParticles;
    }

    public EmitterManualRate(IInputStream stream) throws IOException {
        this.maxParticles = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(maxParticles);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        // nop
    }
}
