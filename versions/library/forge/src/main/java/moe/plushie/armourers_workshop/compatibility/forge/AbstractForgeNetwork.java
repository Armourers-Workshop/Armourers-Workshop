package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IPacketDistributor;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractForgeNetwork {

    public static class Dispatcher extends NetworkManager.Dispatcher {

        public Dispatcher(ResourceLocation channelName, String channelVersion) {
            super(channelName, channelVersion);
        }

        @Override
        public void register() {
            AbstractForgeEventBus.observer(RegisterPayloadHandlerEvent.class, event -> {
                Proxy.ID = channelName;
                IPayloadRegistrar registrar = event.registrar(ModConstants.MOD_ID).versioned(channelVersion);
                registrar.play(channelName, Proxy::new, handler -> {
                    handler.client(this::handleClientData);
                    handler.server(this::handleServerData);
                });
            });
        }

        public void handleServerData(Proxy proxy, PlayPayloadContext context) {
            ServerPlayer player = (ServerPlayer) context.player().orElse(null);
            if (player == null) {
                return;
            }
            IServerPacketHandler packetHandler = context.workHandler()::submitAsync;
            didReceivePacket(packetHandler, proxy.payload, player);
        }

        public void handleClientData(Proxy proxy, PlayPayloadContext context) {
            Player player = context.player().orElse(null);
            if (player == null) {
                return;
            }
            IClientPacketHandler packetHandler = context.workHandler()::submitAsync;
            didReceivePacket(packetHandler, proxy.payload, player);
        }
    }

    public static class Distributor implements IPacketDistributor {

        private final PacketDistributor.PacketTarget target;
        private final CustomPacketPayload packet;

        Distributor(PacketDistributor.PacketTarget target, CustomPacketPayload packet) {
            this.target = target;
            this.packet = packet;
        }

        @Override
        public IPacketDistributor add(ResourceLocation channel, FriendlyByteBuf buf) {
            return new Distributor(target, new Proxy(buf));
        }

        @Override
        public void execute() {
            if (packet == null) {
                return;
            }
            BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(target.flow().getReceptionSide());
            executor.submitAsync(() -> target.send(packet));
        }

        @Override
        public boolean isClientbound() {
            return target.flow().isClientbound();
        }
    }

    public static class Distributors implements NetworkManager.Distributors {

        @Override
        public IPacketDistributor trackingChunk(Supplier<LevelChunk> supplier) {
            return new Distributor(PacketDistributor.TRACKING_CHUNK.with(supplier.get()), null);
        }

        @Override
        public IPacketDistributor trackingEntityAndSelf(Supplier<Entity> supplier) {
            return new Distributor(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(supplier.get()), null);
        }

        @Override
        public IPacketDistributor player(Supplier<ServerPlayer> supplier) {
            return new Distributor(PacketDistributor.PLAYER.with(supplier.get()), null);
        }

        public IPacketDistributor allPlayers() {
            return new Distributor(PacketDistributor.ALL.noArg(), null);
        }

        public IPacketDistributor server() {
            return new Distributor(PacketDistributor.SERVER.noArg(), null);
        }
    }

    public static class Proxy implements CustomPacketPayload {

        public static ResourceLocation ID;
        public final FriendlyByteBuf payload;

        public Proxy(final FriendlyByteBuf buffer) {
            this.payload = buffer;
        }

        @Override
        public void write(FriendlyByteBuf arg) {
            arg.writeBytes(payload);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
