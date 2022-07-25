package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.PacketSplitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class NetworkManagerImpl {

    public static MinecraftServer CURRENT_SERVER;

    private static NetworkDispatcher dispatcher;

    public static void init(String name, String version) {
        dispatcher = new NetworkDispatcher(ArmourersWorkshop.getResource(name));
        // TODO: @SAGESSE Version Checker
//        EventNetworkChannel channel = NetworkRegistry.ChannelBuilder
//                .named(dispatcher.channelName)
//                .networkProtocolVersion(() -> version)
//                .clientAcceptedVersions(version::equals)
//                .serverAcceptedVersions(version::equals)
//                .eventNetworkChannel();
//        channel.registerObject(dispatcher);

        ServerPlayNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onServerEvent);
        EnvironmentExecutor.runWhenOn(EnvironmentType.CLIENT, () -> () -> {
            ClientPlayNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onClientEvent);
        });
    }

    public static void sendToAll(final CustomPacket message) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(PlayerLookup.all(getServer())));
    }

    public static void sendToTracking(final CustomPacket message, final Entity entity) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(PlayerLookup.tracking(entity)));
    }

    public static void sendTo(final CustomPacket message, final ServerPlayer player) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(Collections.singleton(player)));
    }

    @Environment(value = EnvType.CLIENT)
    public static void sendToServer(final CustomPacket message) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            dispatcher.split(message, NetworkDirection.PLAY_TO_SERVER, connection::send);
        }
    }

    private static Consumer<Packet<?>> getSender(Collection<ServerPlayer> players) {
        return packet -> players.forEach(player -> player.connection.send(packet));
    }

    private static MinecraftServer getServer() {
        return CURRENT_SERVER;
    }

    public static class NetworkDispatcher implements IServerPacketHandler, IClientPacketHandler {

        final ResourceLocation channelName;
        final PacketSplitter splitter;

        final int maxPartSize = 32000; // 32k

        NetworkDispatcher(ResourceLocation channelName) {
            this.channelName = channelName;
            this.splitter = new PacketSplitter();
        }

        public void onServerEvent(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            IServerPacketHandler packetHandler = this;
            merge(player.getUUID(), buf, payload -> {
                CustomPacket packet = CustomPacket.fromBuffer(payload);
                server.execute(() -> packet.accept(packetHandler, player));
            });
        }

        @Environment(value = EnvType.CLIENT)
        public void onClientEvent(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
            Player player = client.player;
            if (player == null) {
                // is login retry later.
                buf.retain();
                client.tell(() -> {
                    onClientEvent(client, handler, buf, responseSender);
                    buf.release();
                });
                return;
            }
            IClientPacketHandler packetHandler = this;
            merge(player.getUUID(), buf, payload -> {
                CustomPacket packet = CustomPacket.fromBuffer(payload);
                client.execute(() -> packet.accept(packetHandler, player));
            });
        }

        public void merge(UUID uuid, FriendlyByteBuf buffer, Consumer<FriendlyByteBuf> consumer) {
            splitter.merge(uuid, buffer, consumer);
        }

        public void split(final CustomPacket message, NetworkDirection dir, Consumer<Packet<?>> consumer) {
            int partSize = maxPartSize;
            // download from the server side, the forge is resolved, the maximum packet size is than 10m.
            if (dir == NetworkDirection.PLAY_TO_CLIENT) {
                partSize = Integer.MAX_VALUE;
            }
            splitter.split(message, buf -> dir.buildPacket(buf, channelName), partSize, consumer);
        }
    }

    public enum NetworkDirection {
        PLAY_TO_SERVER(() -> ClientPlayNetworking::createC2SPacket),
        PLAY_TO_CLIENT(() -> ServerPlayNetworking::createS2CPacket);

        final Supplier<BiFunction<ResourceLocation, FriendlyByteBuf, Packet<?>>> builder;

        NetworkDirection(Supplier<BiFunction<ResourceLocation, FriendlyByteBuf, Packet<?>>> builder) {
            this.builder = builder;
        }

        public Packet<?> buildPacket(FriendlyByteBuf buf, ResourceLocation channelName) {
            return builder.get().apply(channelName, buf);
        }
    }
}
