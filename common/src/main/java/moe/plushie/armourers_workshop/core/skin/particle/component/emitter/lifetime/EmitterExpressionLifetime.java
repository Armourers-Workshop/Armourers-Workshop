package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Emitter will turn 'on' when the activation expression is non-zero, and will turn 'off' when it's zero.
 * This is useful for situations like driving an entity-attached emitter from an entity variable.
 */
public class EmitterExpressionLifetime implements SkinParticleComponent {

    /// When the expression is non-zero, the emitter will emit particles.
    /// Evaluated every frame
    private final OpenPrimitive activation;

    /// Emitter will expire if the expression is non-zero.
    /// Evaluated every frame
    private final OpenPrimitive expiration;

    public EmitterExpressionLifetime(OpenPrimitive activation, OpenPrimitive expiration) {
        this.activation = activation;
        this.expiration = expiration;
    }

    public EmitterExpressionLifetime(IInputStream stream) throws IOException {
        this.activation = stream.readPrimitiveObject();
        this.expiration = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(activation);
        stream.writePrimitiveObject(expiration);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        var activation = generator.compile(this.activation, 0.0);
        var expiration = generator.compile(this.expiration, 0.0);
        generator.emitter().tick((emitter, context) -> {
            // stop emit when activation expression result is true.
            if (activation.test(context)) {
                emitter.start();
            }
            // stop emit when expiration expression result is true.
            if (expiration.test(context)) {
                emitter.stop();
            }
        });
    }

    @Override
    public int priority() {
        return -10;
    }
}
