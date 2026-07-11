package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.network.CustomReplyPacket;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.PacketSplitter;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class NetworkManager {

    private static Dispatcher dispatcher;
    private static Distributors distributors;

    public static void init(String name, String version) {
        var instance = PlatformLoader.load(NetworkManager.class);
        dispatcher = instance.createDispatcher(ModConstants.key(name), version);
        distributors = instance.createDistributors();
        dispatcher.register();
    }

    public static void sendToTrackingBlock(final CustomPacket message, final BlockEntity blockEntity) {
        var level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        var chunk = level.getChunkAt(blockEntity.getBlockPos());
        dispatcher.split(message, distributors.trackingChunk(() -> chunk));
    }

    public static void sendToTracking(final CustomPacket message, final Entity entity) {
        dispatcher.split(message, distributors.trackingEntityAndSelf(() -> entity));
    }

    public static void sendTo(final CustomPacket message, final ServerPlayer player) {
        dispatcher.split(message, distributors.player(() -> player));
    }

    public static void sendToServer(final CustomPacket message) {
        dispatcher.split(message, distributors.server());
    }

    public static void sendToAll(final CustomPacket message) {
        dispatcher.split(message, distributors.allPlayers());
    }

    public static void sendWardrobeTo(Entity entity, ServerPlayer player) {
        var wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null) {
            wardrobe.broadcast(player);
        }
    }

    public static <R> void sendTo(final CustomReplyPacket<R> message, final ServerPlayer player, IResultHandler<R> handler) {
        CustomReplyPacket.Receiver.listen(message, handler);
        sendTo(message, player);
    }

    public static <R> void sendToServer(final CustomReplyPacket<R> message, IResultHandler<R> handler) {
        CustomReplyPacket.Receiver.listen(message, handler);
        sendToServer(message);
    }

    public abstract Dispatcher createDispatcher(OpenResourceKey registryName, String version);

    public abstract Distributors createDistributors();

    public static abstract class Dispatcher {

        protected final UUID clientUUID = UUID.randomUUID();
        protected final String channelVersion;
        protected final OpenResourceKey channelName;
        protected final PacketSplitter splitter;

        public Dispatcher(OpenResourceKey channelName, String channelVersion) {
            this.channelName = channelName;
            this.channelVersion = channelVersion;
            this.splitter = new PacketSplitter();
        }


        public abstract void register();

        public void didReceivePacket(IServerPacketHandler packetHandler, IFriendlyByteBuf payload) {
            merge(packetHandler.player().getUUID(), payload, packet -> packetHandler.enqueueWork(() -> {
                packet.accept(packetHandler, packetHandler.player());
                packet.dispose();
            }));
        }

        public void didReceivePacket(IClientPacketHandler packetHandler, IFriendlyByteBuf payload) {
            merge(clientUUID, payload, packet -> packetHandler.enqueueWork(() -> {
                packet.accept(packetHandler, packetHandler.player());
                packet.dispose();
            }));
        }

        public void merge(UUID uuid, IFriendlyByteBuf buffer, Consumer<CustomPacket> consumer) {
            splitter.merge(uuid, buffer, consumer);
        }

        public void split(final CustomPacket message, Distributor distributor) {
            // we need to reserve enough capacity add header/footer data.
            var partSize = getMaximumPayloadSize(distributor) - 256;
            splitter.split(message, buf -> distributor.add(channelName, buf), partSize, Distributor::execute);
        }

        public int getMaximumPayloadSize(Distributor distributor) {
            if (distributor.isClientbound()) {
                return 1048576; // ClientboundCustomPayloadPacket.MAX_PAYLOAD_SIZE
            } else {
                return 32768; // ServerboundCustomPayloadPacket.MAX_PAYLOAD_SIZE
            }
        }
    }

    public interface Distributor {

        Distributor add(OpenResourceKey channel, IFriendlyByteBuf buf);

        void execute();

        boolean isClientbound();

        default boolean isServerbound() {
            return !isClientbound();
        }
    }

    public interface Distributors {

        Distributor trackingChunk(Supplier<LevelChunk> supplier);

        Distributor trackingEntityAndSelf(Supplier<Entity> supplier);

        Distributor player(Supplier<ServerPlayer> supplier);

        Distributor allPlayers();

        Distributor server();
    }
}

