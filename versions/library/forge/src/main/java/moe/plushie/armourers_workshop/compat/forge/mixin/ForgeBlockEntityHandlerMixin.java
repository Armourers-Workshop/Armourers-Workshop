package moe.plushie.armourers_workshop.compat.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeBlockEntity;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Available("[1.21, 1.26)")
@Mixin(AbstractBlockEntity.class)
public abstract class ForgeBlockEntityHandlerMixin implements AbstractForgeBlockEntity {

    @Override
    public AABB getRenderBoundingBox() {
        var blockEntity = AbstractBlockEntity.class.cast(this);
        var result = blockEntity.getVisibleBox(blockEntity.getBlockState());
        if (result != null) {
            return result;
        }
        return AbstractForgeBlockEntity.super.getRenderBoundingBox();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        var blockEntity = AbstractBlockEntity.class.cast(this);
        var dataPacket = blockEntity.getUpdateDataPacket();
        if (dataPacket != null) {
            dataPacket.handle(new TagSerializer(pkt.getTag(), SerializationContext.from(provider)));
        }
    }
}
