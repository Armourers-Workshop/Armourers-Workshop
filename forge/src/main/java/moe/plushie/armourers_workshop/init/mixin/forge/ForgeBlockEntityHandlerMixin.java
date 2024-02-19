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
public interface ForgeBlockEntityHandlerMixin extends AbstractForgeBlockEntity {

    @Override
    default AABB getRenderBoundingBox() {
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        BlockEntity blockEntity = ObjectUtils.unsafeCast(this);
        AABB result = handler.getRenderBoundingBox(blockEntity.getBlockState());
        if (result != null) {
            return result;
        }
        return AbstractForgeBlockEntity.super.getRenderBoundingBox();
    }

    @Override
    default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        BlockEntity blockEntity = ObjectUtils.unsafeCast(this);
        IBlockEntityHandler handler = ObjectUtils.unsafeCast(this);
        handler.handleUpdatePacket(blockEntity.getBlockState(), pkt.getTag());
    }
}
