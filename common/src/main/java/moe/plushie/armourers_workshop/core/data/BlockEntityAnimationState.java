package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.data.action.EntityActionSet;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class BlockEntityAnimationState extends EntityActionSet {

    @Nullable
    public static BlockEntityAnimationState of(@Nullable BlockEntity blockEntity) {
        if (blockEntity != null) {
            return EntityDataStorage.of(blockEntity).getAnimationState().orElse(null);
        }
        return null;
    }

    public void tick(BlockEntity blockEntity) {
        flags.clear();
    }
}
