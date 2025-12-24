package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * This component specifies the dynamic properties of the particle, from a simulation standpoint what forces act upon the particle?
 * These dynamics alter the velocity of the particle, which is a combination of the direction of the particle and the speed.
 * Particle direction will always be in the direction of the velocity of the particle.
 */
public class ParticleDynamicMotion implements SkinParticleComponent {

    /// the linear acceleration applied to the particle, defaults to [0, 0, 0].
    /// Units are blocks/sec/sec
    /// An example would be gravity which is [0, -9.8, 0]
    /// evaluated every frame
    private final OpenPrimitive motionAccelerationX;
    private final OpenPrimitive motionAccelerationY;
    private final OpenPrimitive motionAccelerationZ;

    /// using the equation:
    /// acceleration = -linear_drag_coefficient*velocity
    /// where velocity is the current direction times speed
    /// Think of this as air-drag.  The higher the value, the more drag
    /// evaluated every frame
    private final OpenPrimitive motionDragCoefficient;

    /// acceleration applies to the rotation speed of the particle
    /// think of a disc spinning up or a smoke puff that starts rotating
    /// but slows down over time
    /// evaluated every frame
    /// acceleration is in degrees/sec/sec
    private final OpenPrimitive rotationAcceleration;

    /// drag applied to slow down rotation
    /// equation is rotation_acceleration += -rotation_rate*rotation_drag_coefficient
    /// useful to slow a rotation, or to limit the rotation acceleration
    /// Think of a disc that speeds up (acceleration)
    /// but reaches a terminal speed (drag)
    /// Another use is if you have a particle growing in size, having
    /// the rotation slow down due to drag can add "weight" to the particle's
    /// motion
    private final OpenPrimitive rotationDragCoefficient;

    public ParticleDynamicMotion(OpenPrimitive motionAccelerationX, OpenPrimitive motionAccelerationY, OpenPrimitive motionAccelerationZ, OpenPrimitive motionDragCoefficient, OpenPrimitive rotationAcceleration, OpenPrimitive rotationDragCoefficient) {
        this.motionAccelerationX = motionAccelerationX;
        this.motionAccelerationY = motionAccelerationY;
        this.motionAccelerationZ = motionAccelerationZ;
        this.motionDragCoefficient = motionDragCoefficient;
        this.rotationAcceleration = rotationAcceleration;
        this.rotationDragCoefficient = rotationDragCoefficient;
    }

    public ParticleDynamicMotion(IInputStream stream) throws IOException {
        this.motionAccelerationX = stream.readPrimitiveObject();
        this.motionAccelerationY = stream.readPrimitiveObject();
        this.motionAccelerationZ = stream.readPrimitiveObject();
        this.motionDragCoefficient = stream.readPrimitiveObject();
        this.rotationAcceleration = stream.readPrimitiveObject();
        this.rotationDragCoefficient = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(motionAccelerationX);
        stream.writePrimitiveObject(motionAccelerationY);
        stream.writePrimitiveObject(motionAccelerationZ);
        stream.writePrimitiveObject(motionDragCoefficient);
        stream.writePrimitiveObject(rotationAcceleration);
        stream.writePrimitiveObject(rotationDragCoefficient);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        var motionAccelerationX = generator.compile(this.motionAccelerationX, 0.0);
        var motionAccelerationY = generator.compile(this.motionAccelerationY, 0.0);
        var motionAccelerationZ = generator.compile(this.motionAccelerationZ, 0.0);
        var motionDragCoefficient = generator.compile(this.motionDragCoefficient, 0.0);
        var rotationAcceleration = generator.compile(this.rotationAcceleration, 0.0);
        var rotationDragCoefficient = generator.compile(this.rotationDragCoefficient, 0.0);
        generator.instance().tick((emitter, particle, context) -> {
            var ax = (float) motionAccelerationX.compute(context);
            var ay = (float) motionAccelerationY.compute(context);
            var az = (float) motionAccelerationZ.compute(context);
            var td = (float) motionDragCoefficient.compute(context);
            var ra = (float) rotationAcceleration.compute(context);
            var rd = (float) rotationDragCoefficient.compute(context);

            var acceleration = particle.motionAcceleration().copy();
            acceleration.add(ax, ay, az);
            particle.setMotionAcceleration(acceleration);
            particle.setMotionDrag(td);

            var rotationAcc = particle.rotationAcceleration();
            rotationAcc += ra / 20.0f;
            particle.setRotationAcceleration(rotationAcc);
            particle.setRotationDrag(rd);
        });
    }
}
