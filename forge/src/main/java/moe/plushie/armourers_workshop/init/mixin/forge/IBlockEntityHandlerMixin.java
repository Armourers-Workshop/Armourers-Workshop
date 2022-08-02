package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.api.common.IBlockHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockEntityHandler.class)
public interface IBlockEntityHandlerMixin extends IForgeTileEntity {

    @Override
    default AABB getRenderBoundingBox() {
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        return handler.getCustomRenderBoundingBox();
    }

    default AABB getDefaultRenderBoundingBox() {
        return IForgeTileEntity.super.getRenderBoundingBox();
    }

    @Override
    default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        handler.handleUpdatePacket(getTileEntity().getBlockState(), pkt.getTag());
    }
}
