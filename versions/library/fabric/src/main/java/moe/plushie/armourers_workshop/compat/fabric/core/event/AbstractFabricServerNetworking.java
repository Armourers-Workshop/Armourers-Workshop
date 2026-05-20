package moe.plushie.armourers_workshop.compat.fabric.core.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

@Available("[21, )")
public class AbstractFabricServerNetworking {

    public static void registerQueryReceiver(ConnectionPayloadHandler payloadHandler) {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> payloadHandler.receive(server, handler, synchronizer, sender));
    }

    public static void registerLoginReceiver(OpenResourceKey name, LoginPayloadHandler payloadHandler) {
        ServerLoginNetworking.registerGlobalReceiver(name.get(), (server, handler, understood, buf, synchronizer, seder) -> payloadHandler.receive(server, handler, buf, understood, synchronizer, seder));
    }

    public static <T extends CustomPacketPayload> void registerPlayReceiver(CustomPacketPayload.Type<T> type, PlayPayloadHandler<T> payloadHandler) {
        ServerPlayNetworking.registerGlobalReceiver(type, payloadHandler);
    }

    public interface ConnectionPayloadHandler {
        void receive(MinecraftServer server, ServerLoginPacketListenerImpl handler, ServerLoginNetworking.LoginSynchronizer synchronizer, LoginPacketSender responseSender);
    }

    public interface LoginPayloadHandler {
        void receive(MinecraftServer server, ServerLoginPacketListenerImpl handler, FriendlyByteBuf buf, boolean understood, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender);
    }

    public interface PlayPayloadHandler<T extends CustomPacketPayload> extends ServerPlayNetworking.PlayPayloadHandler<T> {
    }
}
