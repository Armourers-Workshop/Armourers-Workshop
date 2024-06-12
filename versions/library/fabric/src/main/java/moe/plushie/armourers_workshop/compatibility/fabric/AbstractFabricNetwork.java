package moe.plushie.armourers_workshop.compatibility.fabric;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IPacketDistributor;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractFabricNetwork {

    public static class Dispatcher extends NetworkManager.Dispatcher {

        public Dispatcher(IResourceLocation channelName, String channelVersion) {
            super(channelName, channelVersion);
        }

        @Override
        public void register() {
            Proxy.TYPE = new CustomPacketPayload.Type<>(channelName.toLocation());

            PayloadTypeRegistry.playC2S().register(Proxy.TYPE, Proxy.CODEC);
            PayloadTypeRegistry.playS2C().register(Proxy.TYPE, Proxy.CODEC);

            ServerLoginConnectionEvents.QUERY_START.register(this::startServerHandshake);
            ServerLoginNetworking.registerGlobalReceiver(channelName.toLocation(), this::onServerHandshake);
            ServerPlayNetworking.registerGlobalReceiver(Proxy.TYPE, this::onServerEvent);

            EnvironmentExecutor.runOn(EnvironmentType.CLIENT, () -> () -> {
                ClientLoginNetworking.registerGlobalReceiver(channelName.toLocation(), this::onClientHandshake);
                ClientPlayNetworking.registerGlobalReceiver(Proxy.TYPE, this::onClientEvent);
            });
        }

        public void startServerHandshake(Object handler, MinecraftServer server, LoginPacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
            if (ModConfig.Common.enableProtocolCheck) {
                sender.sendPacket(channelName.toLocation(), PacketByteBufs.empty());
            }
        }

        public void onServerHandshake(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
            if (!ModConfig.Common.enableProtocolCheck) {
                return;
            }
            if (understood) {
                String version = buf.readUtf();
                if (version.equals(channelVersion)) {
                    return;
                }
            }
            handler.disconnect(Component.literal("Please install correct Armourers Workshop to play on this server!"));
        }

        @Environment(EnvType.CLIENT)
        public CompletableFuture<@Nullable FriendlyByteBuf> onClientHandshake(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf, Consumer<PacketSendListener> consumer) {
            FriendlyByteBuf responseBuffer = new FriendlyByteBuf(Unpooled.buffer());
            responseBuffer.writeUtf(channelVersion);
            return CompletableFuture.completedFuture(responseBuffer);
        }

        public void onServerEvent(Proxy proxy, ServerPlayNetworking.Context context) {
            IServerPacketHandler packetHandler = context.player().server::execute;
            didReceivePacket(packetHandler, proxy.payload, context.player());
        }

        @Environment(EnvType.CLIENT)
        public void onClientEvent(Proxy proxy, ClientPlayNetworking.Context context) {
            IClientPacketHandler packetHandler = context.client()::execute;
            didReceivePacket(packetHandler, proxy.payload, null);
        }
    }

    public static class Distributor implements IPacketDistributor {

        private final LogicalSide sender;
        private final Consumer<CustomPacketPayload> target;
        private final CustomPacketPayload packet;

        Distributor(LogicalSide sender, Consumer<CustomPacketPayload> target, CustomPacketPayload packet) {
            this.sender = sender;
            this.target = target;
            this.packet = packet;
        }

        @Override
        public IPacketDistributor add(IResourceLocation channel, IFriendlyByteBuf buf) {
            return new Distributor(sender, target, new Proxy(buf));
        }

        @Override
        public void execute() {
            if (packet != null) {
                target.accept(packet);
            }
        }

        @Override
        public boolean isClientbound() {
            return sender.isServer();
        }
    }

    public static class Distributors implements NetworkManager.Distributors {

        @Override
        public IPacketDistributor trackingChunk(Supplier<LevelChunk> supplier) {
            LevelChunk chunk = supplier.get();
            ServerLevel serverLevel = (ServerLevel) chunk.getLevel();
            Collection<ServerPlayer> players = PlayerLookup.tracking(serverLevel, chunk.getPos());
            return new Distributor(LogicalSide.SERVER, dispatch(players), null);
        }

        @Override
        public IPacketDistributor trackingEntityAndSelf(Supplier<Entity> supplier) {
            Entity entity = supplier.get();
            Collection<ServerPlayer> players = PlayerLookup.tracking(entity);
            if (entity instanceof ServerPlayer player) {
                ArrayList<ServerPlayer> trackingAndSelf = new ArrayList<>(players);
                trackingAndSelf.add(player);
                players = trackingAndSelf;
            }
            return new Distributor(LogicalSide.SERVER, dispatch(players), null);
        }

        @Override
        public IPacketDistributor player(Supplier<ServerPlayer> supplier) {
            ServerPlayer player = supplier.get();
            return new Distributor(LogicalSide.SERVER, dispatch(Collections.singleton(player)), null);
        }

        public IPacketDistributor allPlayers() {
            return new Distributor(LogicalSide.SERVER, dispatch(PlayerLookup.all(EnvironmentManager.getServer())), null);
        }

        public IPacketDistributor server() {
            return new Distributor(LogicalSide.CLIENT, ClientPlayNetworking.getSender()::sendPacket, null);
        }

        private Consumer<CustomPacketPayload> dispatch(Collection<ServerPlayer> players) {
            return packet -> players.forEach(player -> ServerPlayNetworking.send(player, packet));
        }
    }

    public enum LogicalSide {

        CLIENT,
        SERVER;

        public boolean isServer() {
            return !isClient();
        }

        public boolean isClient() {
            return this == CLIENT;
        }
    }

    public static class Proxy implements CustomPacketPayload {

        public static Type<Proxy> TYPE;
        public static StreamCodec<RegistryFriendlyByteBuf, Proxy> CODEC = new StreamCodec<>() {
            @Override
            public Proxy decode(RegistryFriendlyByteBuf bufferIn) {
                // we need to tell decoder all data is processed.
                var buffer = bufferIn.retainedSlice();
                var duplicated = new RegistryFriendlyByteBuf(buffer, bufferIn.registryAccess());
                bufferIn.skipBytes(bufferIn.readableBytes());
                return new Proxy(IFriendlyByteBuf.wrap(duplicated));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, Proxy proxy) {
                var sending = proxy.payload.asByteBuf();
                buf.writeBytes(sending.slice());
            }
        };

        public final IFriendlyByteBuf payload;

        public Proxy(final IFriendlyByteBuf buffer) {
            this.payload = buffer;
        }

        @Override
        public Type<Proxy> type() {
            return TYPE;
        }
    }
}
