package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Emitter will loop until it is removed.
 */
public class EmitterLoopingLifetime implements SkinParticleComponent {

    /// emitter will emit particles for this time per loop,
    /// evaluated once per particle emitter loop.
    private final OpenPrimitive activeTime;

    /// emitter will pause emitting particles for this time per loop,
    /// evaluated once per particle emitter loop.
    private final OpenPrimitive sleepTime;

    public EmitterLoopingLifetime(OpenPrimitive activeTime, OpenPrimitive sleepTime) {
        this.activeTime = activeTime;
        this.sleepTime = sleepTime;
    }

    public EmitterLoopingLifetime(IInputStream stream) throws IOException {
        this.activeTime = stream.readPrimitiveObject();
        this.sleepTime = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(activeTime);
        stream.writePrimitiveObject(sleepTime);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var activeTime = compiler.compile(this.activeTime, 10.0);
        var sleepTime = compiler.compile(this.sleepTime, 0.0);
        compiler.emitter().tick((emitter, context) -> {
            var active = activeTime.compute(context);
            var sleep = sleepTime.compute(context);
            var time = emitter.time();
            if (emitter.isRunning() && time >= active) {
                emitter.stop();
            }
            if (!emitter.isRunning() && time >= sleep) {
                emitter.start();
            }
            emitter.setDuration(active);
        });
    }

    @Override
    public int priority() {
        return -10;
    }
}
