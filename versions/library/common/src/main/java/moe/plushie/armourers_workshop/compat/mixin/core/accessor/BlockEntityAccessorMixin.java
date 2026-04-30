package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.blockentity.BlockEntityAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, )")
@Mixin(BlockEntity.class)
public abstract class BlockEntityAccessorMixin implements BlockEntityAccessor {
}
