package moe.plushie.armourers_workshop.core.client.particle;

import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;
import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleInstance;
import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleRenderer;
import moe.plushie.armourers_workshop.core.block.AbstractAttachedHorizontalBlock;
import moe.plushie.armourers_workshop.core.client.animation.bind.ClientExecutionContextImpl;
import moe.plushie.armourers_workshop.core.client.other.EntityPose;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.math.OpenAxisAlignedBoundingBox;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.LazyVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.BlockEntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.EntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticle;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleEmitter;
import moe.plushie.armourers_workshop.core.skin.particle.runtime.LevelAccessor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SmartParticleEmitter implements SkinParticleEmitter, VariableStorage, AbstractParticleInstance, LevelAccessor {

    private static final Name EMITTER_AGE = Name.of("emitter_age");
    private static final Name EMITTER_LIFE_TIME = Name.of("emitter_lifetime");

    private static final Name EMITTER_RANDOM_1 = Name.of("emitter_random_1");
    private static final Name EMITTER_RANDOM_2 = Name.of("emitter_random_2");
    private static final Name EMITTER_RANDOM_3 = Name.of("emitter_random_3");
    private static final Name EMITTER_RANDOM_4 = Name.of("emitter_random_4");

    private float lastPartialTick = 0.0f;
    private float deltaTime = 0.05f;

    private double currentTime = 0.0;
    private double startTime = 0.0;
    private double duration = 0.0;

    private boolean isRunning = false;
    private boolean isEmissiveMode = true;

    private OpenVector3f dimensions = OpenVector3f.ZERO;

    private EntityPose localPose = EntityPose.ZERO;
    private EntityPose localPoseOld = EntityPose.ZERO;

    private EntityPose globalPose = EntityPose.ZERO;
    private EntityPose globalPoseOld = EntityPose.ZERO;

    private final EntityAccessorImpl entity;
    private final AbstractParticleRenderer renderer;

    private final ParticleEmitterUpdater emitter;
    private final ParticleInstanceUpdater instance;

    private final LazyVariableStorage entityStorage = new LazyVariableStorage();
    private final ExecutionContext executionContext = new ClientExecutionContextImpl(this);

    private final ArrayList<SmartParticleInstance> instances = new ArrayList<>();
    private final ArrayDeque<SmartParticleInstance> reusableQueue = new ArrayDeque<>();

    private final ArrayList<OpenAxisAlignedBoundingBox> searchedBoxes = new ArrayList<>();
    private final ArrayList<OpenVoxelShape> collidingShapes = new ArrayList<>();

    private final Float2ObjectOpenHashMap<EntityPose> cachedGlobalPoses = new Float2ObjectOpenHashMap<>();

    public SmartParticleEmitter(ParticleEmitterUpdater emitter, ParticleInstanceUpdater instance, AbstractParticleRenderer renderer, ExecutionContext context) {
        this.entity = EntityAccessorImpl.of(context);
        this.renderer = renderer;
        this.emitter = emitter;
        this.instance = instance;
    }

    @Override
    public SkinParticle spawn() {
        var particle = poll();
        instances.add(particle);
        particle.prepare();
        return particle;
    }

    @Override
    public void start() {
        isRunning = true;
        startTime = currentTime;
    }

    @Override
    public void stop() {
        startTime = currentTime;
        isRunning = false;
    }

    @Override
    public void prepare() {
        // setup predefined variable.
        entityStorage.setVariable(EMITTER_AGE, () -> Result.valueOf(time()));
        entityStorage.setVariable(EMITTER_LIFE_TIME, () -> Result.valueOf(duration()));
        entityStorage.setVariable(EMITTER_RANDOM_1, Result.valueOf(Math.random()));
        entityStorage.setVariable(EMITTER_RANDOM_2, Result.valueOf(Math.random()));
        entityStorage.setVariable(EMITTER_RANDOM_3, Result.valueOf(Math.random()));
        entityStorage.setVariable(EMITTER_RANDOM_4, Result.valueOf(Math.random()));

        // update the current time.
        currentTime = TickUtils.animationTick();

        searchedBoxes.clear();
        collidingShapes.clear();
        cachedGlobalPoses.clear();

        dimensions = entity.getSize(0.0f);

        globalPoseOld = entity.getPose(0.0f);
        globalPose = entity.getPose(1.0f);

        localPoseOld = EntityPose.ZERO;
        localPose = EntityPose.ZERO;

        lastPartialTick = 1.0f;

        // notify emitter prepare event.
        emitter.prepare(this, executionContext);
    }

    @Override
    public void tick() {
        // update the current time.
        currentTime += deltaTime();

        searchedBoxes.clear();
        collidingShapes.clear();
        cachedGlobalPoses.clear();

        globalPoseOld = globalPose;
        globalPose = entity.getPose(1.0f);

        localPoseOld = localPose;

        // notify emitter tick event.
        emitter.tick(this, executionContext);

        // tick all children particle.
        var iterator = instances.iterator();
        while (iterator.hasNext()) {
            var particle = iterator.next();
            particle.tick();
            if (particle.isDead()) {
                recycle(particle);
                iterator.remove();
            }
        }
    }

    @Override
    public void render(AbstractCamera camera, float partialTick, IGraphicsContext context) {
        // update the current time.
        currentTime = TickUtils.animationTick();
        lastPartialTick = partialTick;

        // prepare the particle rendering pipeline.
        renderer.prepare(camera, partialTick, this);

        // notify all alive particle render.
        instances.forEach(SmartParticleInstance::render);

        // notify emitter render event.
        emitter.render(this, executionContext);

        // submit pending elements into scene graphics pipeline.
        renderer.flush(context);

        // draw collision boxes.
        if (ModDebugger.skinParticleCollision) {
            searchedBoxes.forEach(it -> context.draw(ShapeElement.stroke(it, 0xffffff00)));
            collidingShapes.forEach(it -> context.draw(ShapeElement.stroke(it, 0xffff00ff)));
        }
    }

    @Override
    public Block getBlock(OpenVector3f position) {
        var level = entity.level();
        if (level == null) {
            return Blocks.AIR;
        }
        var pos = new OpenVector3i(position);
        var blockState = level.getBlockState(new BlockPos(pos.x, pos.y, pos.z));
        return blockState.getBlock();
    }

    @Override
    public List<OpenVoxelShape> getCollisionBlocks(OpenAxisAlignedBoundingBox box) {
        var level = entity.level();
        if (level == null) {
            return Collections.emptyList();
        }
        var results = level.getCollisions(null, box);
        searchedBoxes.add(box);
        collidingShapes.addAll(results);
        return results;
    }

    @Override
    public boolean isLoaded(OpenVector3f position) {
        var level = entity.level();
        if (level != null) {
            var pos = new OpenVector3i(position);
            return level.isLoaded(new BlockPos(pos.x, pos.y, pos.z));
        }
        return false;
    }

    @Override
    public double time() {
        return currentTime - startTime;
    }

    @Override
    public float deltaTime() {
        return deltaTime;
    }

    @Override
    public double duration() {
        return duration;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isRemoved() {
        return entity.isRemoved();
    }

    @Override
    public boolean isEmissiveMode() {
        return isEmissiveMode;
    }

    @Override
    public List<? extends SkinParticle> particles() {
        return instances;
    }

    @Override
    public OpenVector3f localPosition() {
        return localPosition(lastPartialTick);
    }

    @Override
    public OpenVector3f localPosition(float partialTick) {
        return OpenMath.lerp(partialTick, localPoseOld.position(), localPose.position());
    }

    @Override
    public OpenVector3f globalPosition() {
        return globalPose().position();
    }

    @Override
    public OpenVector3f globalPosition(float partialTick) {
        return globalPose(partialTick).position();
    }

    @Override
    public OpenQuaternionf localRotation() {
        return localRotation(lastPartialTick);
    }

    @Override
    public OpenQuaternionf localRotation(float partialTick) {
        return OpenMath.lerp(partialTick, localPoseOld.rotation(), localPose.rotation());
    }

    @Override
    public OpenQuaternionf globalRotation() {
        return globalPose().rotation();
    }

    @Override
    public OpenQuaternionf globalRotationAt(float partialTick) {
        return globalPose(partialTick).rotation();
    }

    public EntityPose globalPose() {
        return globalPose(lastPartialTick);
    }

    public EntityPose globalPose(float partialTick) {
        return cachedGlobalPoses.computeIfAbsent(partialTick, it -> {
            var globalPose = EntityPose.lerp(it, this.globalPoseOld, this.globalPose);
            var localPose = EntityPose.lerp(it, this.localPoseOld, this.localPose);
            return globalPose.transforming(localPose);
        });
    }

    @Override
    public OpenVector3f size() {
        return dimensions;
    }

    @Override
    public LevelAccessor level() {
        return this;
    }

    @Override
    public float partialTick() {
        return lastPartialTick;
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public void setEmissiveMode(boolean isEmissive) {
        this.isEmissiveMode = isEmissive;
    }

    @Override
    public void setVariable(Name name, Result value) {
        entityStorage.setVariable(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return entityStorage.getVariable(name);
    }

    private void recycle(SmartParticleInstance particle) {
        reusableQueue.push(particle);
    }

    private SmartParticleInstance poll() {
        if (!reusableQueue.isEmpty()) {
            return reusableQueue.pop();
        }
        return new SmartParticleInstance(instance, entityStorage, renderer, this);
    }

    protected static class EntityAccessorImpl {

        private final EntitySelectorImpl<?> entity;
        private final BlockEntitySelectorImpl<?> blockEntity;

        private EntityAccessorImpl(Object entity) {
            this.entity = Objects.safeCast(entity, EntitySelectorImpl.class);
            this.blockEntity = Objects.safeCast(entity, BlockEntitySelectorImpl.class);
        }

        public static EntityAccessorImpl of(ExecutionContext context) {
            return new EntityAccessorImpl(context.entity());
        }


        public EntityPose getPose(float partialTick) {
            var position = getPosition(partialTick);
            var rotation = getRotation(partialTick);
            return EntityPose.of(position, rotation);
        }

        public OpenVector3f getSize(float partialTick) {
            // get dimensions from entity.
            if (entity != null) {
            }
            // get dimensions from block entity.
            if (blockEntity != null) {
            }
            return OpenVector3f.ZERO;
        }

        public OpenVector3f getPosition(float partialTick) {
            // get global location from entity.
            if (entity != null) {
                var x = entity.getX(partialTick);
                var y = entity.getY(partialTick);
                var z = entity.getZ(partialTick);
                return new OpenVector3f(x, y, z);
            }
            // get global location from block entity.
            if (blockEntity != null) {
                var x = blockEntity.getX(partialTick);
                var y = blockEntity.getY(partialTick);
                var z = blockEntity.getZ(partialTick);
                return new OpenVector3f(x, y, z);
            }
            return OpenVector3f.ZERO;
        }

        public OpenQuaternionf getRotation(float partialTick) {
            // get global rotation from entity.
            if (entity != null) {
                var yaw = (float) entity.getHeadYaw(partialTick);
                var pitch = (float) entity.getHeadPitch(partialTick);
                return OpenQuaternionf.fromEulerAnglesYXZ(-yaw, pitch, 0.0f, true);
            }
            // get global rotation from block entity.
            if (blockEntity != null) {
                var state = blockEntity.entity().getBlockState();
                var face = state.getOptionalValue(AbstractAttachedHorizontalBlock.FACE);
                var facing = state.getOptionalValue(AbstractAttachedHorizontalBlock.FACING);
                //var yaw = switch (facing) {
                //    case NORTH -> 0.0f;
                //    case EAST -> 270.0f;
                //    case SOUTH -> 180.0f;
                //    case WEST -> 90.0f;
                //    default -> 0.0f;
                //};
                //return OpenVector3f.YP.rotationDegrees(yaw);
            }
            return OpenQuaternionf.identity();
        }

        public Level level() {
            if (entity != null) {
                return entity.entity().level();
            }
            if (blockEntity != null) {
                return blockEntity.entity().getLevel();
            }
            return null;
        }

        public boolean isRemoved() {
            if (entity != null) {
                return entity.entity().isRemoved();
            }
            if (blockEntity != null) {
                return blockEntity.entity().isRemoved();
            }
            return false;
        }
    }
}
