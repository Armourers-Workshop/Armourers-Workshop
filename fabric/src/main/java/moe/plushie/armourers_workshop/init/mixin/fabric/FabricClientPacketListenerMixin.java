package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class FabricClientPacketListenerMixin {

    @Inject(method = "handleBlockEntityData", at = @At("RETURN"))
    private void hooked_handleBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(packet.getPos());
        if (blockEntity instanceof IBlockEntityHandler) {
            ((IBlockEntityHandler) blockEntity).handleUpdatePacket(blockEntity.getBlockState(), packet.getTag());
        }
    }
}
