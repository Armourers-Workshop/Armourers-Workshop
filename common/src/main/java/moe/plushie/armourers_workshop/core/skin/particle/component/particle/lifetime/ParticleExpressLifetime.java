package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Standard lifetime component.
 * These expressions control the lifetime of the particle.
 */
public class ParticleExpressLifetime implements SkinParticleComponent {

    /// alternate way to express lifetime
    /// particle will expire after this much time
    /// evaluated once
    private final OpenPrimitive duration;

    /// this expression makes the particle expire when true (non-zero)
    /// The float/expr is evaluated once per particle
    /// evaluated every frame
    private final OpenPrimitive expiration;

    public ParticleExpressLifetime(OpenPrimitive duration, OpenPrimitive expiration) {
        this.duration = duration;
        this.expiration = expiration;
    }

    public ParticleExpressLifetime(IInputStream stream) throws IOException {
        this.duration = stream.readPrimitiveObject();
        this.expiration = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(duration);
        stream.writePrimitiveObject(expiration);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        // when the duration not provided, we will use -1s(infinity)
        var duration = compiler.compile(this.duration, -1.0);
        var expiration = compiler.compile(this.expiration, 0.0);
        compiler.instance().prepare((emitter, particle, context) -> {
            var time = duration.compute(context);
            particle.setDuration(time);
        });
        compiler.instance().tick((emitter, particle, context) -> {
            if (expiration.test(context)) {
                particle.kill();
            }
        });
    }

    @Override
    public int priority() {
        return -10;
    }
}
