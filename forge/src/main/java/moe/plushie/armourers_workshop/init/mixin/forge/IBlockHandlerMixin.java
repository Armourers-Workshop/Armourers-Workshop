package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.common.IBlockHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockHandler.class)
public interface IBlockHandlerMixin extends IForgeBlock {

    @Override
    default boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity entity) {
        IBlockHandler handler = ObjectUtils.unsafeCast(this);
        return handler.isCustomBed((Level) level, pos, state, entity);
    }

    @Override
    default boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        IBlockHandler handler = ObjectUtils.unsafeCast(this);
        return handler.isCustomLadder((Level) level, pos, state, entity);
    }
}
