package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockEntityHandler.class)
//#if MC >= 11800
//# public interface IBlockEntityHandlerMixin extends net.minecraftforge.common.extensions.IForgeBlockEntity {
//#else
public interface IBlockEntityHandlerMixin extends net.minecraftforge.common.extensions.IForgeTileEntity {
//#endif

    // @Override from IBlockEntityHandler
    @SuppressWarnings("unused")
    default AABB getDefaultRenderBoundingBox() {
        //#if MC >= 11800
        //# return net.minecraftforge.common.extensions.IForgeBlockEntity.super.getRenderBoundingBox();
        //#else
        return net.minecraftforge.common.extensions.IForgeTileEntity.super.getRenderBoundingBox();
        //#endif
    }

    @Override
    default AABB getRenderBoundingBox() {
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        BlockEntity blockEntity = ObjectUtils.unsafeCast(this);
        return handler.getCustomRenderBoundingBox(blockEntity.getBlockState());
    }

    @Override
    default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        BlockEntity blockEntity = ObjectUtils.unsafeCast(this);
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        handler.handleUpdatePacket(blockEntity.getBlockState(), pkt.getTag());
    }
}
