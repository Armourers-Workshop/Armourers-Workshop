package moe.plushie.armourers_workshop.api.extend;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IExtendedBlockHandler {

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param level  The current world
     * @param blockPos    Block position in world
     * @param blockState  The current state
     * @param entity The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    default boolean isCustomBed(Level level, BlockPos blockPos, BlockState blockState, @Nullable Entity entity) {
        return false;
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param level  The current world
     * @param blockPos    Block position in world
     * @param blockState  The current state
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    default boolean isCustomLadder(Level level, BlockPos blockPos, BlockState blockState, LivingEntity entity) {
        return false;
    }

    /**
     * Called when a player removes a block.  This is responsible for
     * actually destroying the block, and the block is intact at time of call.
     * This is called regardless of whether the player can harvest the block or
     * not.
     * <p>
     * Return true if the block is actually destroyed.
     * <p>
     * Note: When used in multiplayer, this is called on both client and
     * server sides!
     *
     * @param level       The current world
     * @param blockPos    Block position in world.
     * @param blockState  The current state.
     * @param direction   The attack direction.
     * @param player      The player damaging the block.
     * @param hand        The player attack by hand.
     * @return True if the block is actually destroyed.
     */
    default InteractionResult attackBlock(Level level, BlockPos blockPos, BlockState blockState, Direction direction, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

}
