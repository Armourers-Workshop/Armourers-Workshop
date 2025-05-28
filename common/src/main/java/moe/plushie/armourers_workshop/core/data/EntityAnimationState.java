package moe.plushie.armourers_workshop.core.data;


import moe.plushie.armourers_workshop.core.data.action.EntityAction;
import moe.plushie.armourers_workshop.core.data.action.EntityActionSet;
import moe.plushie.armourers_workshop.core.skin.part.wings.WingPartTransform;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

public class EntityAnimationState extends EntityActionSet {

    private Entity transitingVehicle = null;
    private TransitingMode transitingMode = null;

    @Nullable
    public static EntityAnimationState of(@Nullable Entity entity) {
        if (entity != null) {
            return EntityDataStorage.of(entity).getAnimationState().orElse(null);
        }
        return null;
    }

    public void startRiding(Entity target) {
        transitingVehicle = target;
        transitingMode = TransitingMode.UP;
    }

    public void stopRiding(Entity target) {
        transitingVehicle = target;
        transitingMode = TransitingMode.DOWN;
    }

    public void tick(Entity entity) {
        flags.clear();
        if (entity.isSpectator()) {
            return;
        }

        var vehicle = entity.getVehicle();
        if (vehicle == null) {
            vehicle = transitingVehicle;
        }

        // when entity no gravity, everywhere is the ground.
        boolean onGround = entity.onGround() || entity.isNoGravity();

        double dx = entity.getX() - entity.xOld;
        double dy = entity.getY() - entity.yOld;
        double dz = entity.getZ() - entity.zOld;

        boolean isWalk = dx * dx + dz * dz > 2.5e-7;
        boolean isWalkUp = dy > 1e-7;
        boolean isWalkDown = dy < -1e-7;

        boolean isSprinting = entity.isSprinting();
        boolean isCrouching = entity.isCrouching();

        if (vehicle != null) {
            set(EntityAction.RIDING, true);
            set(EntityAction.RIDING_WALK, isWalk);
            set(EntityAction.RIDING_BOOST, isWalk && isSprinting);
            set(EntityAction.RIDING_UP, transitingMode == TransitingMode.UP);
            set(EntityAction.RIDING_DOWN, transitingMode == TransitingMode.DOWN);
            set(EntityAction.RIDING_BOAT, isBoat(vehicle));
            set(EntityAction.RIDING_PIG, isPig(vehicle));
            set(EntityAction.RIDING_HORSE, isHorse(vehicle));
            if (transitingMode != null && entity.getPose() != Pose.CROUCHING) {
                transitingVehicle = null;
                transitingMode = null;
            }
            return;
        }

        if (entity instanceof LivingEntity livingEntity && isFlying(livingEntity)) {
            set(EntityAction.FLYING, true);
            set(EntityAction.FLYING_WALK, isWalk);
            set(EntityAction.FLYING_BOOST, isWalk && isSprinting);
            set(EntityAction.FLYING_UP, isWalkUp);
            set(EntityAction.FLYING_DOWN, isWalkDown);
            set(EntityAction.FLYING_FALL, livingEntity.isFallFlying());
            return;
        }

        if (entity.isInWater()) {
            set(EntityAction.SWIMMING, true);
            set(EntityAction.SWIMMING_WALK, isWalk);
            set(EntityAction.SWIMMING_BOOST, entity.isSwimming());
            set(EntityAction.SWIMMING_UP, isWalkUp);
            set(EntityAction.SWIMMING_DOWN, isWalkDown && !onGround);  // when on ground, can't continue down.
            return;
        }

        if (entity.getPose() == Pose.SWIMMING) {
            set(EntityAction.CRAWLING, true);
            set(EntityAction.CRAWLING_WALK, isWalk);
            return;
        }

        if (entity instanceof LivingEntity livingEntity && livingEntity.onClimbable()) {
            set(EntityAction.CLIMBING, true);
            set(EntityAction.CLIMBING_WALK, isWalk);
            set(EntityAction.CLIMBING_UP, isWalkUp && !onGround);
            set(EntityAction.CLIMBING_DOWN, isWalkDown && !onGround && !isCrouching);  // when hold shift, can't continue down.
            set(EntityAction.CLIMBING_HOLD, isCrouching && !onGround && !isWalkUp); // when move up, can't continue hold.
            return;
        }

        set(EntityAction.WALK, isWalk);
        set(EntityAction.RUNNING, isSprinting);
        set(EntityAction.SNEAK, isCrouching);
        set(EntityAction.JUMP, !onGround);
    }

    private boolean isFlying(LivingEntity entity) {
        return WingPartTransform.isFlying(entity);
    }

    private boolean isBoat(Entity entity) {
        return entity instanceof Boat;
    }

    private boolean isPig(Entity entity) {
        return entity instanceof Pig;
    }

    private boolean isHorse(Entity entity) {
        return entity instanceof AbstractHorse;
    }

    public enum TransitingMode {
        UP, DOWN
    }
}
