package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeBlockEntity;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockEntityHandler.class)
public interface IBlockEntityHandlerMixin extends AbstractForgeBlockEntity {

    // @Override from IBlockEntityHandler
    @SuppressWarnings("unused")
    default AABB getDefaultRenderBoundingBox() {
        return AbstractForgeBlockEntity.super.getRenderBoundingBox();
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
