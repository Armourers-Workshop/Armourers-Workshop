package moe.plushie.armourers_workshop.core.skin.particle.component.emitter;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

/**
 * This component specifies the frame of reference of the emitter.
 * Applies only when the emitter is attached to an entity.
 * When 'position' is true, the particles will simulate in entity space, otherwise they will simulate in world space.
 * Rotation works the same way for rotation.
 * Default is false for both, which makes the particles emit relative to the emitter, then simulate independently from the emitter.
 * Note that rotation = true and position = false is an invalid option.
 * Velocity will add the emitter's velocity to the initial particle velocity.
 */
public class EmitterInitialLocalSpace implements SkinParticleComponent {

    /// When enabled, the particle will always move in local space relative to the emitter. When attached to an entity, this means that all particles will move along with the entity.
    /// When disabled, particles are emitted relative to the emitter, then simulate independently from the emitter in the world
    /// Enabling this will prevent collisions with the world from working.
    private final boolean position;

    /// Rotate the local space along with the entity that it is attached to. See Local Position
    /// Local position needs to be enabled for local rotation to work
    private final boolean rotation;

    /// When enabled, the emitter's velocity will be added to the initial particle velocity.
    private final boolean velocity;

    public EmitterInitialLocalSpace(boolean position, boolean rotation, boolean velocity) {
        this.position = position;
        this.rotation = rotation;
        this.velocity = velocity;
    }

    public EmitterInitialLocalSpace(IInputStream stream) throws IOException {
        this.position = stream.readBoolean();
        this.rotation = stream.readBoolean();
        this.velocity = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeBoolean(position);
        stream.writeBoolean(rotation);
        stream.writeBoolean(velocity);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        generator.instance().prepare((emitter, particle, context) -> {
            particle.setRelativeMode(position, rotation, velocity);
        });
    }

    @Override
    public int priority() {
        return 1000;
    }
}
