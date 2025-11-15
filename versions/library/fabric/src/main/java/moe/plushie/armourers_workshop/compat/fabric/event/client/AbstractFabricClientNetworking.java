package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

@Available("[1.21, )")
public class AbstractFabricClientNetworking {

    public static void registerLoginReceiver(ResourceLocation name, LoginPayloadHandler payloadHandler) {
        ClientLoginNetworking.registerGlobalReceiver(name, (client, handler, buf, sender) -> payloadHandler.receive(client, handler, buf, null));
    }

    public static <T extends CustomPacketPayload> void registerPlayReceiver(CustomPacketPayload.Type<T> type, PlayPayloadHandler<T> payloadHandler) {
        ClientPlayNetworking.registerGlobalReceiver(type, payloadHandler);
    }

    public interface LoginPayloadHandler {
        CompletableFuture<FriendlyByteBuf> receive(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf, Object consumer);
    }

    public interface PlayPayloadHandler<T extends CustomPacketPayload> extends ClientPlayNetworking.PlayPayloadHandler<T> {
    }
}
