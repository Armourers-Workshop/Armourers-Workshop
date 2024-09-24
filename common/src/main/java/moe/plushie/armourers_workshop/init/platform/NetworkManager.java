package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IPacketDistributor;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.network.CustomReplyPacket;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.PacketSplitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class NetworkManager {

    private static Dispatcher dispatcher;
    private static Distributors distributors;

    public static void init(String name, String version) {
        dispatcher = createDispatcher(ModConstants.key(name), version);
        distributors = createDistributors();
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
        CustomReplyPacket.Receiver.await(message, handler);
        sendTo(message, player);
    }

    public static <R> void sendToServer(final CustomReplyPacket<R> message, IResultHandler<R> handler) {
        CustomReplyPacket.Receiver.await(message, handler);
        sendToServer(message);
    }

    @ExpectPlatform
    public static Dispatcher createDispatcher(IResourceLocation registryName, String version) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Distributors createDistributors() {
        throw new AssertionError();
    }

    public static abstract class Dispatcher {

        protected final UUID clientUUID = UUID.randomUUID();
        protected final String channelVersion;
        protected final IResourceLocation channelName;
        protected final PacketSplitter splitter;

        protected final int maxPartSize = 32000; // 32k

        public Dispatcher(IResourceLocation channelName, String channelVersion) {
            this.channelName = channelName;
            this.channelVersion = channelVersion;
            this.splitter = new PacketSplitter();
        }

        public abstract void register();

        public void didReceivePacket(IServerPacketHandler packetHandler, IFriendlyByteBuf payload, ServerPlayer player) {
            merge(player.getUUID(), payload, packet -> packetHandler.enqueueWork(() -> {
                packet.accept(packetHandler, player);
            }));
        }

        @Environment(EnvType.CLIENT)
        public void didReceivePacket(IClientPacketHandler packetHandler, IFriendlyByteBuf payload, Player player) {
            merge(clientUUID, payload, packet -> packetHandler.enqueueWork(() -> {
                packet.accept(packetHandler, EnvironmentManager.getPlayer());
            }));
        }

        public void merge(UUID uuid, IFriendlyByteBuf buffer, Consumer<CustomPacket> consumer) {
            splitter.merge(uuid, buffer, consumer);
        }

        public void split(final CustomPacket message, IPacketDistributor distributor) {
            int partSize = maxPartSize;
            // download from the server side, the forge is resolved, the maximum packet size is than 10m.
            if (distributor.isClientbound()) {
                partSize = Integer.MAX_VALUE;
            }
            splitter.split(message, buf -> distributor.add(channelName, buf), partSize, IPacketDistributor::execute);
        }
    }

    public interface Distributors {

        IPacketDistributor trackingChunk(Supplier<LevelChunk> supplier);

        IPacketDistributor trackingEntityAndSelf(Supplier<Entity> supplier);

        IPacketDistributor player(Supplier<ServerPlayer> supplier);

        IPacketDistributor allPlayers();

        IPacketDistributor server();
    }
}

