package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundBlockEntityDataPacket.class)
public class FabricBlockEntityUpdatePacketMixin {

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("RETURN"))
    private void aw2$handleBlockEntityData(ClientGamePacketListener clientGamePacketListener, CallbackInfo ci) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        ClientboundBlockEntityDataPacket packet = ObjectUtils.unsafeCast(this);
        BlockEntity blockEntity = level.getBlockEntity(packet.getPos());
        if (blockEntity instanceof IBlockEntityHandler) {
            AbstractDataSerializer serializer = AbstractDataSerializer.wrap(packet.getTag(), level.registryAccess());
            ((IBlockEntityHandler) blockEntity).handleUpdatePacket(blockEntity.getBlockState(), serializer);
        }
    }
}
