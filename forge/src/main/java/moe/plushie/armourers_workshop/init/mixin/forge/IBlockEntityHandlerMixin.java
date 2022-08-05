package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockEntityHandler.class)
public interface IBlockEntityHandlerMixin extends IForgeTileEntity {

    @Override
    default AABB getRenderBoundingBox() {
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        return handler.getCustomRenderBoundingBox();
    }

    // @Override from IBlockEntityHandler
    @SuppressWarnings("unused")
    default AABB getDefaultRenderBoundingBox() {
        return IForgeTileEntity.super.getRenderBoundingBox();
    }

    @Override
    default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        handler.handleUpdatePacket(getTileEntity().getBlockState(), pkt.getTag());
    }
}
