package moe.plushie.armourers_workshop.core.data;


import joptsimple.internal.Strings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;

public class EntityActionSet {

    private final BitSet flags = new BitSet(EntityAction.values().length);

    private Entity transitingVehicle = null;
    private TransitingMode transitingMode = null;

    @Nullable
    public static EntityActionSet of(@Nullable Entity entity) {
        if (entity != null) {
            return EntityDataStorage.of(entity).getActionSet().orElse(null);
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

        boolean onGround = entity.onGround();

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

    public void set(EntityAction action, boolean value) {
        if (value) {
            flags.set(action.ordinal());
        }
    }

    public boolean get(EntityAction action) {
        if (action == EntityAction.IDLE) {
            return flags.isEmpty();
        }
        return flags.get(action.ordinal());
    }

    private boolean isFlying(LivingEntity entity) {
        // ii
        if (entity instanceof Player player && player.getAbilities().flying) {
            return true;
        }
        return entity.isFallFlying();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityActionSet that)) return false;
        return flags.equals(that.flags);
    }

    @Override
    public int hashCode() {
        return flags.hashCode();
    }

    public EntityActionSet copy() {
        var result = new EntityActionSet();
        result.flags.or(flags);
        return result;
    }

    @Override
    public String toString() {
        var prefix = "";
        var lists = new LinkedHashMap<String, ArrayList<String>>();
        var results = new StringBuilder();
        for (var flag : EntityAction.values()) {
            if (get(flag)) {
                var parts = flag.name().toLowerCase().split("_");
                var sp = lists.computeIfAbsent(parts[0], k -> new ArrayList<>());
                sp.addAll(Arrays.asList(parts).subList(1, parts.length));
            }
        }
        for (var entry : lists.entrySet()) {
            results.append(prefix);
            results.append(entry.getKey());
            prefix = "; ";
            if (!entry.getValue().isEmpty()) {
                results.append("[");
                results.append(Strings.join(entry.getValue(), ","));
                results.append("]");
            }
        }
        return results.toString();
    }

    public enum TransitingMode {
        UP, DOWN
    }
}
