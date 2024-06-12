package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IPacketDistributor;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractForgeNetwork {

    public static class Dispatcher extends NetworkManager.Dispatcher {

        public Dispatcher(IResourceLocation channelName, String channelVersion) {
            super(channelName, channelVersion);
        }

        @Override
        public void register() {
            AbstractForgeEventBus.observer(RegisterPayloadHandlersEvent.class, event -> {
                PayloadRegistrar registrar = event.registrar(ModConstants.MOD_ID).versioned(channelVersion);
                Proxy.TYPE = new CustomPacketPayload.Type<>(channelName.toLocation());
                registrar.playBidirectional(Proxy.TYPE, Proxy.CODEC, this::handBidirectionalData);
            });
        }

        public void handBidirectionalData(Proxy proxy, IPayloadContext context) {
            if (context.flow().isServerbound()) {
                handleServerboundData(proxy, context);
            } else {
                handleClientboundData(proxy, context);
            }
        }

        public void handleServerboundData(Proxy proxy, IPayloadContext context) {
            ServerPlayer player = (ServerPlayer) context.player();
            IServerPacketHandler packetHandler = context::enqueueWork;
            didReceivePacket(packetHandler, proxy.payload, player);
        }

        public void handleClientboundData(Proxy proxy, IPayloadContext context) {
            IClientPacketHandler packetHandler = context::enqueueWork;
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
            if (packet == null) {
                return;
            }
            BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(sender);
            executor.submitAsync(() -> target.accept(packet));
        }

        @Override
        public boolean isClientbound() {
            return sender.isServer();
        }
    }

    public static class Distributors implements NetworkManager.Distributors {

        @Override
        public IPacketDistributor trackingChunk(Supplier<LevelChunk> supplier) {
            return new Distributor(LogicalSide.SERVER, msg -> PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) supplier.get().getLevel(), supplier.get().getPos(), msg), null);
        }

        @Override
        public IPacketDistributor trackingEntityAndSelf(Supplier<Entity> supplier) {
            return new Distributor(LogicalSide.SERVER, msg -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(supplier.get(), msg), null);
        }

        @Override
        public IPacketDistributor player(Supplier<ServerPlayer> supplier) {
            return new Distributor(LogicalSide.SERVER, msg -> PacketDistributor.sendToPlayer(supplier.get(), msg), null);
        }

        public IPacketDistributor allPlayers() {
            return new Distributor(LogicalSide.SERVER, msg -> PacketDistributor.sendToAllPlayers(msg), null);
        }

        public IPacketDistributor server() {
            return new Distributor(LogicalSide.CLIENT, msg -> PacketDistributor.sendToServer(msg), null);
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
