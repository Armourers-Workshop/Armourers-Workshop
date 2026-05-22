package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenSize3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.particle.runtime.LevelAccessor;

import java.util.List;

public interface SkinParticleEmitter {

    /// Spawn a new particle instance.
    SkinParticle spawn();

    /// Start emit particle, the time will be reset to 0.
    void start();

    /// Stop emit particle, the time will be reset to 0.
    void stop();

    /// The alive time.
    double time();

    /// Each tick delta time.
    float deltaTime();

    /// The duration time.
    double duration();

    /// The emitter running state.
    boolean isRunning();

    /// The emitter is emissive mode.
    boolean isEmissiveMode();

    /// The all alive particles.
    List<? extends SkinParticle> particles();

    /// The emitter current local position.
    OpenVector3f localPosition();

    /// The emitter local position at the partial ticks.
    OpenVector3f localPosition(float partialTick);

    /// The emitter current global position.
    OpenVector3f globalPosition();

    /// The emitter global position at the partial ticks.
    OpenVector3f globalPosition(float partialTick);

    /// The emitter current local rotation.
    OpenQuaternionf localRotation();

    /// The emitter local rotation at the partial ticks.
    OpenQuaternionf localRotation(float partialTick);

    /// The emitter current global rotation.
    OpenQuaternionf globalRotation();

    /// The emitter global rotation at the partial ticks.
    OpenQuaternionf globalRotationAt(float partialTick);

    /// The emitter attached entity size.
    OpenVector3f size();

    /// The emitter attached entity level.
    LevelAccessor level();

    /// The emitter current partial ticks.
    float partialTick();

    /// Update the emitter duration.
    void setDuration(double duration);

    /// Update the emitter emissive mode.
    void setEmissiveMode(boolean isEmissive);
}
