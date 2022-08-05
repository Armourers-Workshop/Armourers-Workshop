package moe.plushie.armourers_workshop.init.platform.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EntityClimbingEvents {

    public static final Event<AllowClimbing> ALLOW_CLIMBING = EventFactory.createArrayBacked(AllowClimbing.class, callbacks -> (entity, blockPos, blockState) -> {
        for (AllowClimbing callback : callbacks) {
            InteractionResult result = callback.allowClimbing(entity, blockPos, blockState);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return InteractionResult.PASS;
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
        InteractionResult allowClimbing(LivingEntity entity, BlockPos blockPos, BlockState blockState);
    }
}
