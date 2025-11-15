package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.Nullable;

@Available("[1.21, 1.22)")
public class AbstractForgeNetworkImpl {

    public static <T extends CustomPacketPayload> void register(String channel, String version, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, IPayloadHandler<T> serverHandler, @Nullable IPayloadHandler<T> clientHandler) {
        AbstractForgeEventBus.observer(RegisterPayloadHandlersEvent.class, event -> {
            var registrar = event.registrar(channel).versioned(version);
            registrar.playBidirectional(type, codec, (payload, context) -> {
                if (context.flow().isServerbound()) {
                    if (serverHandler != null) {
                        serverHandler.handle(payload, context);
                    }
                } else {
                    if (clientHandler != null) {
                        clientHandler.handle(payload, context);
                    }
                }
            });
        });
    }
}
