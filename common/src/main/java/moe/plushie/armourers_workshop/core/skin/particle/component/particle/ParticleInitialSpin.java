package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Starts the particle with a specified orientation and rotation rate.
 */
public class ParticleInitialSpin implements SkinParticleComponent {

    /// specifies the initial rotation in degrees
    /// evaluated once
    private final OpenPrimitive rotation;

    /// specifies the spin rate in degrees/second
    /// evaluated once
    private final OpenPrimitive rotationRate;

    public ParticleInitialSpin(OpenPrimitive rotation, OpenPrimitive rotationRate) {
        this.rotation = rotation;
        this.rotationRate = rotationRate;
    }

    public ParticleInitialSpin(IInputStream stream) throws IOException {
        this.rotation = stream.readPrimitiveObject();
        this.rotationRate = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(rotation);
        stream.writePrimitiveObject(rotationRate);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var rotation = compiler.compile(this.rotation, 0.0);
        var rotationRate = compiler.compile(this.rotationRate, 0.0);
        compiler.instance().prepare((emitter, particle, context) -> {
            var rot = (float) rotation.compute(context);
            var velocity = (float) rotationRate.compute(context);
            particle.setRotationInitial(rot);
            particle.setRotationVelocity(velocity / 20.0f);
        });
    }
}
