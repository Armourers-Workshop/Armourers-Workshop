package moe.plushie.armourers_workshop.init.platform.fabric.event;

import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("unused")
public final class EntityLifecycleEvents {

    public static final Event<EntityTrackingEvents.StartTracking> WILL_START_TRACKING = EntityTrackingEvents.START_TRACKING;

    public static final Event<EntityTrackingEvents.StopTracking> DID_STOP_TRACKING = EntityTrackingEvents.STOP_TRACKING;

    public static final Event<EntityTrackingEvents.StartTracking> DID_START_TRACKING = EventFactory.createArrayBacked(EntityTrackingEvents.StartTracking.class, callbacks -> (trackedEntity, player) -> {
        for (var callback : callbacks) {
            callback.onStartTracking(trackedEntity, player);
        }
    });

    public static final Event<AllowBed> ALLOW_BED = EventFactory.createArrayBacked(AllowBed.class, callbacks -> (entity, sleepingPos, state, vanillaResult) -> {
        for (var callback : callbacks) {
            var result = callback.allowBed(entity, sleepingPos, state, vanillaResult);
            if (result != OpenInteractionResult.PASS) {
                return result;
            }
        }
        return OpenInteractionResult.PASS;
    });

    public static final Event<StopSleeping> STOP_SLEEPING = EventFactory.createArrayBacked(StopSleeping.class, callbacks -> (entity, sleepingPos) -> {
        for (var callback : callbacks) {
            callback.onStopSleeping(entity, sleepingPos);
        }
    });

    public static final Event<UseBlock> USE_BLOCK = EventFactory.createArrayBacked(UseBlock.class, callbacks -> (player, level, hand, hitResult) -> {
        for (var callback : callbacks) {
            var result = callback.interact(player, level, hand, hitResult);
            if (result != OpenInteractionResult.PASS) {
                return result;
            }
        }
        return OpenInteractionResult.PASS;
    });

    public static final Event<AttackBlock> ATTACK_BLOCK = EventFactory.createArrayBacked(AttackBlock.class, callbacks -> (player, level, hand, pos, direction) -> {
        for (var callback : callbacks) {
            var result = callback.interact(player, level, hand, pos, direction);
            if (result != OpenInteractionResult.PASS) {
                return result;
            }
        }
        return OpenInteractionResult.PASS;
    });

    public static final Event<AllowClimbing> ALLOW_CLIMBING = EventFactory.createArrayBacked(AllowClimbing.class, callbacks -> (entity, blockPos, blockState) -> {
        for (var callback : callbacks) {
            var result = callback.allowClimbing(entity, blockPos, blockState);
            if (result != OpenInteractionResult.PASS) {
                return result;
            }
        }
        return OpenInteractionResult.PASS;
    });

    public static final Event<Size> SIZE = EventFactory.createArrayBacked(Size.class, callbacks -> (entity, pose, oldSize, newSize) -> {
        for (var callback : callbacks) {
            newSize = callback.resize(entity, pose, oldSize, newSize);
        }
        return newSize;
    });

    @FunctionalInterface
    public interface AllowClimbing {
        /**
         * Checks whether a player's spawn can be set when sleeping.
         *
         * @param entity   the sleeping player
         * @param blockPos the sleeping position
         * @return true if allowed, false otherwise
         */
        OpenInteractionResult allowClimbing(LivingEntity entity, BlockPos blockPos, BlockState blockState);
    }

    @FunctionalInterface
    public interface AllowBed {

        /**
         * Checks whether a block is a valid bed for the entity.
         *
         * @param entity        the sleeping entity
         * @param sleepingPos   the position of the block
         * @param state         the block state to check
         * @param vanillaResult {@code true} if vanilla allows the block, {@code false} otherwise
         */
        OpenInteractionResult allowBed(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult);
    }

    @FunctionalInterface
    public interface StopSleeping {
        /**
         * Called when an entity stops sleeping and wakes up.
         *
         * @param entity      the sleeping entity
         * @param sleepingPos the {@linkplain LivingEntity#getSleepingPos() sleeping position} of the entity
         */
        void onStopSleeping(LivingEntity entity, BlockPos sleepingPos);
    }

    @FunctionalInterface
    public interface UseBlock {

        OpenInteractionResult interact(Player player, Level level, InteractionHand hand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    public interface AttackBlock {

        OpenInteractionResult interact(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction);
    }

    @FunctionalInterface
    public interface Size {

        EntityDimensions resize(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize);
    }
}
