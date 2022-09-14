package moe.plushie.armourers_workshop.core.permission;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockPermissionContext extends PlayerPermissionContext {

    public final BlockPos blockPos;
    public final BlockState blockState;
    public final Direction facing;

    public BlockPermissionContext(Player ep, BlockPos pos, @Nullable BlockState state, @Nullable Direction f) {
        super(ep);
        this.blockPos = Preconditions.checkNotNull(pos, "BlockPos can't be null in BlockPosContext!");
        this.blockState = state;
        this.facing = f;
    }
}
