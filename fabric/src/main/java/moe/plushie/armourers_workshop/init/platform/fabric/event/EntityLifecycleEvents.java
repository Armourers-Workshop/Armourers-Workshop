package moe.plushie.armourers_workshop.init.platform.fabric.event;

import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public final class EntityLifecycleEvents {

    public static final Event<EntityTrackingEvents.StartTracking> WILL_START_TRACKING = EntityTrackingEvents.START_TRACKING;

    public static final Event<EntityTrackingEvents.StopTracking> DID_STOP_TRACKING = EntityTrackingEvents.STOP_TRACKING;

    public static final Event<EntityTrackingEvents.StartTracking> DID_START_TRACKING = EventFactory.createArrayBacked(EntityTrackingEvents.StartTracking.class, callbacks -> (trackedEntity, player) -> {
        for (var callback : callbacks) {
            callback.onStartTracking(trackedEntity, player);
        }
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
    public interface Size {

        EntityDimensions resize(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize);
    }
}
