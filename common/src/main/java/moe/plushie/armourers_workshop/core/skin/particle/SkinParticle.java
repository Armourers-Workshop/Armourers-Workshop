package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.particle.runtime.GraphicsAccessor;
import moe.plushie.armourers_workshop.core.skin.particle.runtime.LevelAccessor;

public interface SkinParticle {

    void kill();


    default boolean isAlive() {
        return !isDead();
    }

    boolean isDead();

    boolean isManualMode();


    /// The particle alive time.
    double time();

    /// The particle duration time.
    double duration();

    /// The particle current local position.
    OpenVector3f position();

    /// The particle local position at the partial ticks.
    OpenVector3f positionAt(float partialTick);

    /// The particle current local rotation.
    OpenQuaternionf rotation();

    /// The particle local rotation at the partial ticks.
    OpenQuaternionf rotationAt(float partialTick);

    OpenVector3f speed();


    float motionDrag();

    OpenVector3f motionAcceleration();

    OpenVector3f accelerationFactor();

    float rotationInitial();

    float rotationVelocity();

    float rotationDrag();

    float rotationAcceleration();


    int tintColor();

    /// The particle attached entity level.
    LevelAccessor level();

    /// The particle attached render pipeline.
    GraphicsAccessor pipeline();

    /// The particle current partial ticks.
    float partialTick();


    void setDuration(double duration);


    void setPosition(OpenVector3f position);

    default void setPosition(float x, float y, float z) {
        setPosition(new OpenVector3f(x, y, z));
    }

    void setSpeed(OpenVector3f speed);


    void setMotionDrag(float motionDrag);

    void setMotionAcceleration(OpenVector3f motionAcceleration);

    void setAccelerationFactor(OpenVector3f accelerationFactor);

    void setRotationInitial(float initialRotation);

    void setRotationVelocity(float rotationVelocity);

    void setRotationDrag(float rotationDrag);

    void setRotationAcceleration(float rotAcceleration);

    void setTintColor(float r, float g, float b, float a);

    void setManualMode(boolean manual);

    void setRelativeMode(boolean position, boolean rotation, boolean velocity);
}
