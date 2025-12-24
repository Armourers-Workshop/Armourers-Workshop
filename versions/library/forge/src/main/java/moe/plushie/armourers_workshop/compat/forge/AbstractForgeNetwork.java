package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractForgeNetwork {

    public static class Dispatcher extends NetworkManager.Dispatcher {

        public Dispatcher(OpenResourceLocation channelName, String channelVersion) {
            super(channelName, channelVersion);
        }

        @Override
        public void register() {
            Proxy.TYPE = new CustomPacketPayload.Type<>(channelName.get());
            AbstractForgeNetworkImpl.register(channelName.namespace(), channelVersion, Proxy.TYPE, Proxy.CODEC, this::handleServerboundData, this::handleClientboundData);
        }

        public void handleServerboundData(Proxy proxy, IPayloadContext context) {
            var payload = proxy.payload().slice();
            var packetHandler = new IServerPacketHandler() {

                @Override
                public ServerPlayer player() {
                    return (ServerPlayer) context.player();
                }

                @Override
                public void enqueueWork(Runnable work) {
                    context.enqueueWork(work);
                }
            };
            didReceivePacket(packetHandler, payload);
        }

        public void handleClientboundData(Proxy proxy, IPayloadContext context) {
            var payload = proxy.payload().slice();
            var packetHandler = new IClientPacketHandler() {

                @Override
                public Player player() {
                    return context.player();
                }

                @Override
                public void enqueueWork(Runnable work) {
                    context.enqueueWork(work);
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
        public Distributor add(OpenResourceLocation channel, IFriendlyByteBuf buf) {
            return new Distributor(sender, target, new Proxy(buf));
        }

        @Override
        public void execute() {
            if (packet != null) {
                AbstractForgeWorkQueue.get(sender).submitAsync(() -> target.accept(packet));
            }
        }

        @Override
        public boolean isClientbound() {
            return sender.isServer();
        }
    }

    @SuppressWarnings("Convert2MethodRef")
    public static class Distributors implements NetworkManager.Distributors {

        @Override
        public Distributor trackingChunk(Supplier<LevelChunk> supplier) {
            return new Distributor(LogicalSide.SERVER, msg -> Connection.sendToPlayersTrackingChunk((ServerLevel) supplier.get().getLevel(), supplier.get().getPos(), msg), null);
        }

        @Override
        public Distributor trackingEntityAndSelf(Supplier<Entity> supplier) {
            return new Distributor(LogicalSide.SERVER, msg -> Connection.sendToPlayersTrackingEntityAndSelf(supplier.get(), msg), null);
        }

        @Override
        public Distributor player(Supplier<ServerPlayer> supplier) {
            return new Distributor(LogicalSide.SERVER, msg -> Connection.sendToPlayer(supplier.get(), msg), null);
        }

        @Override
        public Distributor allPlayers() {
            return new Distributor(LogicalSide.SERVER, msg -> Connection.sendToAllPlayers(msg), null);
        }

        @Override
        public Distributor server() {
            return new Distributor(LogicalSide.CLIENT, msg -> Connection.sendToServer(msg), null);
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

        private final IFriendlyByteBuf payload;

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
