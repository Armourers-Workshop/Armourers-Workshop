package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Emitter will execute once, and once the lifetime ends or the number of particles allowed to emit have emitted, the emitter expires.
 */
public class EmitterOnceLifetime implements SkinParticleComponent {

    /// how long the particles emit for evaluated once.
    private final OpenPrimitive activeTime;

    public EmitterOnceLifetime(OpenPrimitive activeTime) {
        this.activeTime = activeTime;
    }

    public EmitterOnceLifetime(IInputStream stream) throws IOException {
        this.activeTime = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(activeTime);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var activeTime = compiler.compile(this.activeTime, 10.0);
        compiler.emitter().tick((emitter, context) -> {
            var active = activeTime.compute(context);
            var time = emitter.time();
            if (time >= active) {
                emitter.stop();
            }
            emitter.setDuration(active);
        });
    }

    @Override
    public int priority() {
        return -10;
    }
}
