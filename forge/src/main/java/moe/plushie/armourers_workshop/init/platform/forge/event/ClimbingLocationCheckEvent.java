package moe.plushie.armourers_workshop.init.platform.forge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class ClimbingLocationCheckEvent extends LivingEvent {
    private final BlockPos climbingLocation;
    private final BlockState climbingState;

    public ClimbingLocationCheckEvent(LivingEntity player, BlockPos climbingLocation, BlockState climbingState) {
        super(player);
        this.climbingLocation = climbingLocation;
        this.climbingState = climbingState;
    }

    public BlockPos getClimbingLocation() {
        return this.climbingLocation;
    }

    public BlockState getClimbingState() {
        return this.climbingState;
    }
}

