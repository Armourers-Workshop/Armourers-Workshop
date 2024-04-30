package moe.plushie.armourers_workshop.compatibility.mixin;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractFriendlyByteBuf;
import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Available("[1.21, )")
@Mixin(ProtocolInfoBuilder.class)
public class ProtocolInfoBuilderMixin {

    @Inject(method = "serverboundProtocolUnbound", at = @At("RETURN"), cancellable = true)
    private static <T extends ServerboundPacketListener, B extends ByteBuf> void aw2$serverboundProtocolUnbound(ConnectionProtocol connectionProtocol, Consumer<ProtocolInfoBuilder<T, B>> consumer, CallbackInfoReturnable<ProtocolInfo.Unbound<T, B>> cir) {
        ProtocolInfo.Unbound<T, B> oldValue = cir.getReturnValue();
        cir.setReturnValue(transformer -> {
            AbstractFriendlyByteBuf.setServerboundTransformer(transformer);
            return oldValue.bind(transformer);
        });
    }

    @Inject(method = "clientboundProtocolUnbound", at = @At("RETURN"), cancellable = true)
    private static <T extends ClientboundPacketListener, B extends ByteBuf> void aw2$clientboundProtocolUnbound(ConnectionProtocol connectionProtocol, Consumer<ProtocolInfoBuilder<T, B>> consumer, CallbackInfoReturnable<ProtocolInfo.Unbound<T, B>> cir) {
        ProtocolInfo.Unbound<T, B> oldValue = cir.getReturnValue();
        cir.setReturnValue(transformer -> {
            AbstractFriendlyByteBuf.setClientboundTransformer(transformer);
            return oldValue.bind(transformer);
        });
    }
}
