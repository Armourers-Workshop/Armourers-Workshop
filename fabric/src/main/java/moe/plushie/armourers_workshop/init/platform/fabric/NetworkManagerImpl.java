package moe.plushie.armourers_workshop.init.platform.fabric;

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.PacketSplitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class NetworkManagerImpl implements NetworkManager.Impl {

    public static MinecraftServer CURRENT_SERVER;

    private NetworkDispatcher dispatcher;

    public static NetworkManager.Impl getInstance(String name, String version) {
        NetworkManagerImpl impl = new NetworkManagerImpl();
        impl.init(name, version);
        return impl;
    }

    public void init(String name, String version) {
        dispatcher = new NetworkDispatcher(ArmourersWorkshop.getResource(name), version);

        ServerLoginConnectionEvents.QUERY_START.register(dispatcher::startServerHandshake);
        ServerLoginNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onServerHandshake);

        ServerPlayNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onServerEvent);

        EnvironmentExecutor.runWhenOn(EnvironmentType.CLIENT, () -> () -> {
            ClientLoginNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onClientHandshake);
            ClientPlayNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onClientEvent);
        });
    }

    @Override
    public void sendToAll(final CustomPacket message) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(PlayerLookup.all(getServer())));
    }

    @Override
    public void sendToTracking(final CustomPacket message, final Entity entity) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(PlayerLookup.tracking(entity)));
    }

    @Override
    public void sendTo(final CustomPacket message, final ServerPlayer player) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(Collections.singleton(player)));
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void sendToServer(final CustomPacket message) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            dispatcher.split(message, NetworkDirection.PLAY_TO_SERVER, connection::send);
        }
    }

    private Consumer<Packet<?>> getSender(Collection<ServerPlayer> players) {
        return packet -> players.forEach(player -> player.connection.send(packet));
    }

    private MinecraftServer getServer() {
        return CURRENT_SERVER;
    }

    public static class NetworkDispatcher implements IServerPacketHandler, IClientPacketHandler {

        final String channelVersion;
        final ResourceLocation channelName;
        final PacketSplitter splitter;

        final int maxPartSize = 32000; // 32k

        NetworkDispatcher(ResourceLocation channelName, String channelVersion) {
            this.channelName = channelName;
            this.channelVersion = channelVersion;
            this.splitter = new PacketSplitter();
        }

        public void startServerHandshake(ServerLoginPacketListenerImpl handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
            sender.sendPacket(channelName, PacketByteBufs.empty());
        }

        public void onServerHandshake(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
            if (understood) {
                String version = buf.readUtf(Short.MAX_VALUE);
                if (version.equals(channelVersion)) {
                    return;
                }
            }
            handler.disconnect(new TextComponent("Please install correct Armourers Workshop to play on this server!"));
        }

        @Environment(value = EnvType.CLIENT)
        public CompletableFuture<@Nullable FriendlyByteBuf> onClientHandshake(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
            FriendlyByteBuf responseBuffer = new FriendlyByteBuf(Unpooled.buffer());
            responseBuffer.writeUtf(channelVersion);
            return CompletableFuture.completedFuture(responseBuffer);
        }

        public void onServerEvent(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            IServerPacketHandler packetHandler = this;
            merge(player.getUUID(), buf, packet -> server.execute(() -> packet.accept(packetHandler, player)));
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
            merge(player.getUUID(), buf, packet -> client.execute(() -> packet.accept(packetHandler, player)));
        }

        public void merge(UUID uuid, FriendlyByteBuf buffer, Consumer<CustomPacket> consumer) {
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
