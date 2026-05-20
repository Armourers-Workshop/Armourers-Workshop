package moe.plushie.armourers_workshop.init.platform.fabric.event.common;

import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface FabricEntityEvent {

    LivingEntity entity();

    /**
     * Checks whether a block is a valid bed for the entity.
     */
    interface StartSleep extends FabricEntityEvent {

        /**
         * the position of the block.
         */
        BlockPos sleepingPos();

        /**
         * the block state to check.
         */
        BlockState blockState();

        void setResult(OpenInteractionResult result);
    }

    /**
     * Called when an entity stops sleeping and wakes up.
     */
    interface StopSleep extends FabricEntityEvent {

        /**
         * the position of the block.
         */
        BlockPos sleepingPos();

    }

    /**
     * Checks whether an entity is allowed to climb the block.
     */
    interface StartClimbing extends FabricEntityEvent {

        /**
         * the position of the block.
         */
        BlockPos blockPos();

        /**
         * the block state to check.
         */
        BlockState blockState();

        void setResult(OpenInteractionResult result);
    }

    interface UseBlock extends FabricEntityEvent {

        Level level();

        OpenInteractionHand hand();

        BlockHitResult hitResult();

        void setResult(OpenInteractionResult result);
    }

    interface AttackBlock extends FabricEntityEvent {

        Level level();

        OpenInteractionHand hand();

        BlockPos pos();

        OpenDirection direction();

        void setResult(OpenInteractionResult result);
    }
}
