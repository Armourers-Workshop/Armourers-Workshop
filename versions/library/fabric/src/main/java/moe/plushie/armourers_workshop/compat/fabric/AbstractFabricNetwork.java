package moe.plushie.armourers_workshop.compat.fabric;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricClientNetworking;
import moe.plushie.armourers_workshop.compat.fabric.event.common.AbstractFabricServerNetworking;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[21, )")
public class AbstractFabricNetwork {

    public static class Dispatcher extends NetworkManager.Dispatcher {

        public Dispatcher(OpenResourceKey channelName, String channelVersion) {
            super(channelName, channelVersion);
        }

        @Override
        public void register() {
            Proxy.TYPE = new CustomPacketPayload.Type<>(channelName.get());

            PayloadTypeRegistry.playC2S().register(Proxy.TYPE, Proxy.CODEC);
            PayloadTypeRegistry.playS2C().register(Proxy.TYPE, Proxy.CODEC);

            AbstractFabricServerNetworking.registerQueryReceiver(this::onServerQueryEvent);
            AbstractFabricServerNetworking.registerLoginReceiver(channelName.get(), this::onServerLoginEvent);
            AbstractFabricServerNetworking.registerPlayReceiver(Proxy.TYPE, this::onServerPlayEvent);

            EnvironmentExecutor.runOnClient(() -> () -> {
                AbstractFabricClientNetworking.registerLoginReceiver(channelName.get(), this::onClientLoginEvent);
                AbstractFabricClientNetworking.registerPlayReceiver(Proxy.TYPE, this::onClientEvent);
            });
        }

        public void onServerQueryEvent(MinecraftServer server, ServerLoginPacketListenerImpl handler, ServerLoginNetworking.LoginSynchronizer synchronizer, LoginPacketSender sender) {
            if (ModConfig.Common.enableProtocolCheck) {
                sender.sendPacket(channelName.get(), PacketByteBufs.empty());
            }
        }

        public void onServerLoginEvent(MinecraftServer server, ServerLoginPacketListenerImpl handler, FriendlyByteBuf buf, boolean understood, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
            if (!ModConfig.Common.enableProtocolCheck) {
                return;
            }
            if (understood) {
                var version = buf.readUtf();
                if (version.equals(channelVersion)) {
                    return;
                }
            }
            handler.disconnect(Component.literal("Please install correct Armourers Workshop to play on this server!"));
        }

        public void onServerPlayEvent(Proxy proxy, ServerPlayNetworking.Context context) {
            var payload = proxy.payload().slice();
            var packetHandler = new IServerPacketHandler() {

                @Override
                public ServerPlayer player() {
                    return context.player();
                }

                @Override
                public void enqueueWork(Runnable work) {
                    context.server().execute(work);
                }
            };
            didReceivePacket(packetHandler, payload);
        }

        @Environment(EnvType.CLIENT)
        public CompletableFuture<FriendlyByteBuf> onClientLoginEvent(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf, Object consumer) {
            var responseBuffer = new FriendlyByteBuf(Unpooled.buffer());
            responseBuffer.writeUtf(channelVersion);
            return CompletableFuture.completedFuture(responseBuffer);
        }

        @Environment(EnvType.CLIENT)
        public void onClientEvent(Proxy proxy, ClientPlayNetworking.Context context) {
            var payload = proxy.payload().slice();
            var packetHandler = new IClientPacketHandler() {

                @Override
                public Player player() {
                    return context.client().player;
                }

                @Override
                public void enqueueWork(Runnable work) {
                    context.client().execute(work);
                }
            };
            didReceivePacket(packetHandler, payload);
        }
    }

    public static class Distributor implements NetworkManager.Distributor {

        private final LogicalSide sender;
        private final Consumer<CustomPacketPayload> target;
        private final CustomPacketPayload packet;

        Distributor(LogicalSide sender, Consumer<CustomPacketPayload> target, CustomPacketPayload packet) {
            this.sender = sender;
            this.target = target;
            this.packet = packet;
        }

        @Override
        public Distributor add(OpenResourceKey channel, IFriendlyByteBuf buf) {
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
        public Distributor trackingChunk(Supplier<LevelChunk> supplier) {
            var chunk = supplier.get();
            var serverLevel = (ServerLevel) chunk.getLevel();
            var players = PlayerLookup.tracking(serverLevel, chunk.getPos());
            return new Distributor(LogicalSide.SERVER, dispatch(players), null);
        }

        @Override
        public Distributor trackingEntityAndSelf(Supplier<Entity> supplier) {
            var entity = supplier.get();
            var players = PlayerLookup.tracking(entity);
            if (entity instanceof ServerPlayer player) {
                var trackingAndSelf = new ArrayList<>(players);
                trackingAndSelf.add(player);
                players = trackingAndSelf;
            }
            return new Distributor(LogicalSide.SERVER, dispatch(players), null);
        }

        @Override
        public Distributor player(Supplier<ServerPlayer> supplier) {
            var player = supplier.get();
            return new Distributor(LogicalSide.SERVER, dispatch(Collections.singleton(player)), null);
        }

        @Override
        public Distributor allPlayers() {
            return new Distributor(LogicalSide.SERVER, dispatch(PlayerLookup.all(EnvironmentManager.getServer())), null);
        }

        @Override
        public Distributor server() {
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

        public IFriendlyByteBuf payload() {
            return payload;
        }
    }
}
