package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Starts the particle with a specified speed, using the direction specified by the emitter shape.
 */
public class ParticleInitialSpeed implements SkinParticleComponent {

    /// evaluated once
    private final OpenPrimitive speed;

    public ParticleInitialSpeed(OpenPrimitive speed) {
        this.speed = speed;
    }

    public ParticleInitialSpeed(IInputStream stream) throws IOException {
        this.speed = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(speed);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        var speed = generator.compile(this.speed, 1.0);
        generator.instance().prepare((emitter, particle, context) -> {
            var scale = (float) speed.compute(context);
            particle.setSpeed(particle.speed().scaling(scale));
        });
    }

    @Override
    public int priority() {
        return 5;
    }
}
