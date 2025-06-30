package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundBlockEntityDataPacket.class)
public class FabricBlockEntityUpdatePacketMixin {

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("RETURN"))
    private void aw2$handleBlockEntityData(ClientGamePacketListener clientGamePacketListener, CallbackInfo ci) {
        var level = EnvironmentManager.getClient().level;
        if (level == null) {
            return;
        }
        var packet = ClientboundBlockEntityDataPacket.class.cast(this);
        var blockEntity = level.getBlockEntity(packet.getPos());
        if (blockEntity instanceof IBlockEntityHandler entityHandler) {
            var serializer = new TagSerializer(packet.getTag(), level);
            entityHandler.handleUpdatePacket(blockEntity.getBlockState(), serializer);
        }
    }
}
