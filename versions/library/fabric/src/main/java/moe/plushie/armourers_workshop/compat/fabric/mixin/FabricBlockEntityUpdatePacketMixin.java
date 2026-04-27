package moe.plushie.armourers_workshop.compat.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[16, )")
@Mixin(ClientboundBlockEntityDataPacket.class)
public class FabricBlockEntityUpdatePacketMixin {

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("RETURN"))
    private void aw2$handleBlockEntityData(ClientGamePacketListener clientGamePacketListener, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        var packet = ClientboundBlockEntityDataPacket.class.cast(this);
        var blockEntity = level.getBlockEntity(packet.getPos());
        if (blockEntity instanceof AbstractBlockEntity blockEntity1) {
            var packet1 = blockEntity1.getUpdateDataPacket();
            if (packet1 != null) {
                packet1.handle(new TagSerializer(packet.getTag(), SerializationContext.from(blockEntity)));
            }
        }
    }
}
