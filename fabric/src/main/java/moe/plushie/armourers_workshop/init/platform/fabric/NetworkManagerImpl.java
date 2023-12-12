package moe.plushie.armourers_workshop.init.platform.fabric;

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.PacketSplitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class NetworkManagerImpl implements NetworkManager.Impl {

    private NetworkDispatcher dispatcher;

    public static NetworkManager.Impl getInstance(String name, String version) {
        NetworkManagerImpl impl = new NetworkManagerImpl();
        impl.init(name, version);
        return impl;
    }

    public void init(String name, String version) {
        dispatcher = new NetworkDispatcher(ModConstants.key(name), version);

        ServerLoginConnectionEvents.QUERY_START.register(dispatcher::startServerHandshake);
        ServerLoginNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onServerHandshake);

        ServerPlayNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onServerEvent);

        EnvironmentExecutor.runWhenOn(EnvironmentType.CLIENT, () -> () -> {
            ClientLoginNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onClientHandshake);
            ClientPlayNetworking.registerGlobalReceiver(dispatcher.channelName, dispatcher::onClientEvent);
        });
    }

    @Override
    public void sendToTrackingChunk(CustomPacket message, LevelChunk chunk) {
        ServerLevel serverLevel = (ServerLevel) chunk.getLevel();
        Collection<ServerPlayer> players = PlayerLookup.tracking(serverLevel, chunk.getPos());
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(players));
    }

    @Override
    public void sendToTracking(final CustomPacket message, final Entity entity) {
        Collection<ServerPlayer> players = PlayerLookup.tracking(entity);
        if (entity instanceof ServerPlayer) {
            ArrayList<ServerPlayer> trackingAndSelf = new ArrayList<>(players);
            trackingAndSelf.add((ServerPlayer) entity);
            players = trackingAndSelf;
        }
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(players));
    }

    @Override
    public void sendTo(final CustomPacket message, final ServerPlayer player) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(Collections.singleton(player)));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void sendToServer(final CustomPacket message) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_SERVER, ClientPlayNetworking.getSender()::sendPacket);
    }

    @Override
    public void sendToAll(CustomPacket message) {
        dispatcher.split(message, NetworkDirection.PLAY_TO_CLIENT, getSender(PlayerLookup.all(EnvironmentManager.getServer())));
    }

    private Consumer<Packet<?>> getSender(Collection<ServerPlayer> players) {
        return packet -> players.forEach(player -> player.connection.send(packet));
    }

    public static class NetworkDispatcher implements IServerPacketHandler, IClientPacketHandler {

        final UUID clientUUID = UUID.randomUUID();
        final String channelVersion;
        final ResourceLocation channelName;
        final PacketSplitter splitter;

        final int maxPartSize = 32000; // 32k

        NetworkDispatcher(ResourceLocation channelName, String channelVersion) {
            this.channelName = channelName;
            this.channelVersion = channelVersion;
            this.splitter = new PacketSplitter();
        }

        public void startServerHandshake(Object handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
            if (ModConfig.Common.enableProtocolCheck) {
                sender.sendPacket(channelName, PacketByteBufs.empty());
            }
        }

        public void onServerHandshake(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
            if (!ModConfig.Common.enableProtocolCheck) {
                return;
            }
            if (understood) {
                String version = buf.readUtf(Short.MAX_VALUE);
                if (version.equals(channelVersion)) {
                    return;
                }
            }
            handler.disconnect(Component.literal("Please install correct Armourers Workshop to play on this server!"));
        }

        @Environment(EnvType.CLIENT)
        public CompletableFuture<@Nullable FriendlyByteBuf> onClientHandshake(Minecraft client, Object handler, FriendlyByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
            FriendlyByteBuf responseBuffer = new FriendlyByteBuf(Unpooled.buffer());
            responseBuffer.writeUtf(channelVersion);
            return CompletableFuture.completedFuture(responseBuffer);
        }

        public void onServerEvent(MinecraftServer server, ServerPlayer player, Object handler, FriendlyByteBuf buf, PacketSender responseSender) {
            IServerPacketHandler packetHandler = this;
            merge(player.getUUID(), buf, packet -> server.execute(() -> packet.accept(packetHandler, player)));
        }

        @Environment(EnvType.CLIENT)
        public void onClientEvent(Minecraft client, Object handler, FriendlyByteBuf buf, PacketSender responseSender) {
            IClientPacketHandler packetHandler = this;
            merge(clientUUID, buf, packet -> client.execute(() -> packet.accept(packetHandler, getClientPlayer())));
        }

        @Environment(EnvType.CLIENT)
        public Player getClientPlayer() {
            // a better solution is use the player directly, but it's a trap.
            // java will generate an anonymous lambda for we source code,
            // and then it will load lambda type on the server environment.
            return Minecraft.getInstance().player;
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
