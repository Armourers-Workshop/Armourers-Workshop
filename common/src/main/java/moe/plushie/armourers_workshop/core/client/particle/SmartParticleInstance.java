package moe.plushie.armourers_workshop.core.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleRenderer;
import moe.plushie.armourers_workshop.core.client.animation.bind.ClientExecutionContextImpl;
import moe.plushie.armourers_workshop.core.client.other.EntityPose;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.LazyVariableStorage;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticle;
import moe.plushie.armourers_workshop.core.skin.particle.runtime.GraphicsAccessor;
import moe.plushie.armourers_workshop.core.skin.particle.runtime.LevelAccessor;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.TickUtils;

@OnlyIn(Dist.CLIENT)
public class SmartParticleInstance implements SkinParticle, VariableStorage {

    private static final Name PARTICLE_AGE = Name.of("particle_age");
    private static final Name PARTICLE_LIFE_TIME = Name.of("particle_lifetime");

    private static final Name PARTICLE_RANDOM_1 = Name.of("particle_random_1");
    private static final Name PARTICLE_RANDOM_2 = Name.of("particle_random_2");
    private static final Name PARTICLE_RANDOM_3 = Name.of("particle_random_3");
    private static final Name PARTICLE_RANDOM_4 = Name.of("particle_random_4");

    private int tintColor = -1;

    private float rotationInitial = 0.0f;
    private float rotationVelocity = 0.0f;
    private float rotationAcceleration = 0.0f;
    private float rotationDrag = 0.0f;

    private float drag = 0;
    private float dragFactor = 0;

    private double currentTime = 0.0;
    private double startTime = 0.0;
    private double duration = 0.0;

    private boolean isDead = false;
    private boolean isManualMode = false;

    private boolean relativePositionMode = false;
    private boolean relativeRotationMode = false;
    private boolean relativeVelocityMode = false;

    private OpenVector3f speed = OpenVector3f.ONE;

    private OpenVector3f acceleration = OpenVector3f.ZERO;
    private OpenVector3f accelerationFactor = OpenVector3f.ONE;

    private EntityPose localPose = EntityPose.ZERO;
    private EntityPose localPoseOld = EntityPose.ZERO;

    private EntityPose initialGlobalPose;

    private final LazyVariableStorage entityStorage;
    private final ExecutionContext executionContext;

    private final SmartParticleEmitter emitter;
    private final ParticleInstanceUpdater instance;

    private final AbstractParticleRenderer renderer;

    public SmartParticleInstance(ParticleInstanceUpdater instance, LazyVariableStorage storage, AbstractParticleRenderer renderer, SmartParticleEmitter emitter) {
        this.emitter = emitter;
        this.instance = instance;
        this.entityStorage = storage.copy();
        this.executionContext = new ClientExecutionContextImpl(this);
        this.renderer = renderer;
    }

    public void prepare() {
        // setup predefined variable.
        entityStorage.setVariable(PARTICLE_AGE, () -> Result.valueOf(time()));
        entityStorage.setVariable(PARTICLE_LIFE_TIME, () -> Result.valueOf(duration()));
        entityStorage.setVariable(PARTICLE_RANDOM_1, Result.valueOf(Math.random()));
        entityStorage.setVariable(PARTICLE_RANDOM_2, Result.valueOf(Math.random()));
        entityStorage.setVariable(PARTICLE_RANDOM_3, Result.valueOf(Math.random()));
        entityStorage.setVariable(PARTICLE_RANDOM_4, Result.valueOf(Math.random()));

        localPose = EntityPose.ZERO;
        localPoseOld = EntityPose.ZERO;
        initialGlobalPose = EntityPose.of(emitter.globalPosition(), emitter.globalRotation());

        rotationInitial = 0.0f;
        rotationVelocity = 0.0f;
        rotationAcceleration = 0.0f;
        rotationDrag = 0.0f;

        drag = 0;
        dragFactor = 0;

        currentTime = TickUtils.animationTick();
        startTime = currentTime;
        duration = 0;

        isDead = false;
        isManualMode = false;

        relativePositionMode = false;
        relativeRotationMode = false;
        relativeVelocityMode = false;

        tintColor = -1;

        speed = OpenVector3f.ONE;

        acceleration = OpenVector3f.ZERO;
        accelerationFactor = OpenVector3f.ONE;

        // notify the particle prepare event.
        instance.prepare(emitter, this, executionContext);

//            if (particle.relativePosition && !particle.relativeRotation)
//            {
//                Vector3f vec = new Vector3f(particle.position);
//
//                particle.matrix.transform(vec);
//                particle.position.x = vec.x;
//                particle.position.y = vec.y;
//                particle.position.z = vec.z;
//            }
//
//            if (!(particle.relativePosition && particle.relativeRotation))
//            {
//                particle.position.add(this.lastGlobal);
//                particle.initialPosition.add(this.lastGlobal);
//            }
//
//            particle.rotation = particle.initialRotation;
//            particle.prevPosition.set(particle.position);
//            particle.prevRotation = particle.rotation;
        localPoseOld = localPose;
    }

//        public Matrix3f matrix = new Matrix3f();
//        public void setupMatrix(BedrockEmitter emitter) {
//            if (this.relativePosition)
//            {
//                if (this.relativeRotation)
//                {
//                    this.matrix.setIdentity();
//                }
//                else if (!this.matrixSet)
//                {
//                    this.matrix.set(emitter.rotation);
//                    this.matrixSet = true;
//                }
//            }
//            else if (this.relativeRotation)
//            {
//                this.matrix.set(emitter.rotation);
//            }
//        }

    public void tick() {
        // update the current time.
        currentTime = TickUtils.animationTick();

        localPoseOld = localPose;

//            this.setupMatrix(emitter);

        if (!isManualMode()) {
            float rotationAcceleration = this.rotationAcceleration / 20F - this.rotationDrag * this.rotationVelocity;
            this.rotationVelocity += rotationAcceleration / 20F;
            var localRotation = rotationInitial + rotationVelocity * (float) time();

//                /* Position */
            var xx = -(drag + dragFactor);
            var tx1 = speed.x * xx;
            var ty1 = speed.y * xx;
            var tz1 = speed.z * xx;

//                this.acceleration.add(vec);
//                this.acceleration.scale(1 / 20F);
            acceleration.add(tx1, ty1, tz1);
            acceleration.scale(1 / 20f);
//                this.speed.add(this.acceleration);
            speed.add(acceleration);

//                vec.set(this.speed);
//                vec.x *= this.accelerationFactor.x;
//                vec.y *= this.accelerationFactor.y;
//                vec.z *= this.accelerationFactor.z;
            var tx = speed.x * accelerationFactor.x;
            var ty = speed.y * accelerationFactor.y;
            var tz = speed.z * accelerationFactor.z;
//
//                if (this.relativePosition || this.relativeRotation)
//                {
//                    this.matrix.transform(vec);
//                }
//
//                this.position.x += vec.x / 20F;
//                this.position.y += vec.y / 20F;
//                this.position.z += vec.z / 20F;
            var localPosition = localPose.position().adding(tx / 20f, ty / 20f, tz / 20.f);

            localPose = EntityPose.of(localPosition, OpenVector3f.ZP.rotationDegrees(localRotation));
        }

        if (duration >= 0 && time() >= duration) {
            kill();
        }

        instance.tick(emitter, this, executionContext);
    }

    public void render() {
        // update the current time.
        currentTime = TickUtils.animationTick();

        // when the relative position mode is true means the particles need to follow the movement of the emitter.
        var globalPosition = initialGlobalPose.position();
        if (relativePositionMode) {
            globalPosition = null;
        }
        // when the relative rotTion mode is true means the particles need to follow the yaw/pitch of the emitter.
        var globalRotation = initialGlobalPose.rotation();
        if (relativeRotationMode) {
            globalRotation = null;
        }

        renderer.setTintColor(tintColor);
        renderer.setInitialPosition(globalPosition);
        renderer.setInitialRotation(globalRotation);

        // notify particle render event.
        instance.render(emitter, this, executionContext);
    }

    @Override
    public void kill() {
        isDead = true;
    }

    @Override
    public boolean isDead() {
        return isDead || emitter.level() == null;
    }

    @Override
    public boolean isManualMode() {
        return isManualMode;
    }

    @Override
    public double time() {
        return currentTime - startTime;
    }

    @Override
    public double duration() {
        return duration;
    }

    @Override
    public OpenVector3f position() {
        return positionAt(partialTick());
    }

    @Override
    public OpenVector3f positionAt(float partialTick) {
        return OpenMath.lerp(partialTick, localPoseOld.position(), localPose.position());
    }

    @Override
    public OpenQuaternionf rotation() {
        return rotationAt(partialTick());
    }

    @Override
    public OpenQuaternionf rotationAt(float partialTick) {
        return OpenMath.lerp(partialTick, localPoseOld.rotation(), localPose.rotation());
    }

    @Override
    public OpenVector3f speed() {
        return speed;
    }

    public float motionDrag() {
        return drag;
    }

    @Override
    public OpenVector3f motionAcceleration() {
        return acceleration;
    }

    @Override
    public OpenVector3f accelerationFactor() {
        return accelerationFactor;
    }

    @Override
    public float rotationInitial() {
        return this.rotationInitial;
    }

    @Override
    public float rotationVelocity() {
        return this.rotationVelocity;
    }

    @Override
    public float rotationDrag() {
        return this.rotationDrag;
    }

    @Override
    public void setRotationAcceleration(float rotAcceleration) {
        this.rotationAcceleration = rotAcceleration;
    }

    @Override
    public float rotationAcceleration() {
        return this.rotationAcceleration;
    }

    @Override
    public int tintColor() {
        return tintColor;
    }

    @Override
    public LevelAccessor level() {
        return emitter.level();
    }

    @Override
    public GraphicsAccessor pipeline() {
        return renderer;
    }

    @Override
    public float partialTick() {
        return emitter.partialTick();
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public void setSpeed(OpenVector3f speed) {
        this.speed = speed;
    }

    @Override
    public void setPosition(OpenVector3f position) {
        this.localPose = localPose.withPosition(position);
    }

    @Override
    public void setMotionDrag(float motionDrag) {
        this.drag = motionDrag;
    }

    @Override
    public void setMotionAcceleration(OpenVector3f motionAcceleration) {
        this.acceleration = motionAcceleration;
    }

    @Override
    public void setAccelerationFactor(OpenVector3f accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }

    @Override
    public void setRotationInitial(float initialRotation) {
        this.rotationInitial = initialRotation;
    }

    @Override
    public void setRotationDrag(float rotationDrag) {
        this.rotationDrag = rotationDrag;
    }

    @Override
    public void setRotationVelocity(float rotationVelocity) {
        this.rotationVelocity = rotationVelocity;
    }

    @Override
    public void setTintColor(float r, float g, float b, float a) {
        this.tintColor = Colors.toARGB(r, g, b, a);
    }

    @Override
    public void setManualMode(boolean manual) {
        this.isManualMode = manual;
    }

    @Override
    public void setRelativeMode(boolean position, boolean rotation, boolean velocity) {
        this.relativePositionMode = position;
        this.relativeRotationMode = rotation;
        this.relativeVelocityMode = velocity;
    }

    @Override
    public void setVariable(Name name, Result value) {
        entityStorage.setVariable(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return entityStorage.getVariable(name);
    }
}
