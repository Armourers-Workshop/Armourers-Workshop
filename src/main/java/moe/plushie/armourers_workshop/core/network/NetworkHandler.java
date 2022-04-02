package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.core.network.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkHandler {

    private static final HashMap<Class<? extends CustomPacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<>();
    private static NetworkHandler INSTANCE;

    private final ResourceLocation channelName;
    private final ExecutorService executor = Executors.newFixedThreadPool(1, r -> {
        Thread thread = new Thread(r, "Network-Data-Coder");
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    });

    public NetworkHandler(final ResourceLocation channelName) {

        EventNetworkChannel channel = NetworkRegistry.ChannelBuilder.named(channelName).networkProtocolVersion(() -> "1").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).eventNetworkChannel();

        channel.registerObject(this);

        this.channelName = channelName;
    }

    public static void init(final ResourceLocation channelName) {
        INSTANCE = new NetworkHandler(channelName);
    }

    public static NetworkHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onServerEvent(final NetworkEvent.ClientCustomPayloadEvent event) {
        NetworkEvent.Context context = event.getSource().get();
        optimized(event.getPayload(), payload -> {
            ServerPlayNetHandler netHandler = (ServerPlayNetHandler) context.getNetworkManager().getPacketListener();
            CustomPacket packet = CustomPacket.fromBuffer(payload);
            context.enqueueWork(() -> packet.accept(netHandler, netHandler.player));
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientEvent(final NetworkEvent.ServerCustomPayloadEvent event) {
        if (event instanceof NetworkEvent.ServerCustomPayloadLoginEvent) {
            return;
        }
        NetworkEvent.Context context = event.getSource().get();
        optimized(event.getPayload(), payload -> {
            ClientPlayNetHandler netHandler = (ClientPlayNetHandler) context.getNetworkManager().getPacketListener();
            CustomPacket packet = CustomPacket.fromBuffer(payload);
            context.enqueueWork(() -> packet.accept(netHandler, Minecraft.getInstance().player));
        });
        context.setPacketHandled(true);
    }

    public ResourceLocation getChannel() {
        return this.channelName;
    }

    public void sendToAll(final CustomPacket message) {
        executor.submit(() -> {
            IPacket<?> packet = message.buildPacket(NetworkDirection.PLAY_TO_CLIENT);
            getServer().getPlayerList().broadcastAll(packet);
        });
    }

    public void sendTo(final CustomPacket message, final ServerPlayerEntity player) {
        executor.submit(() -> {
            IPacket<?> packet = message.buildPacket(NetworkDirection.PLAY_TO_CLIENT);
            player.connection.send(packet);
        });
    }

//    public void sendToAllAround(final BasePacket message, final PacketDistributor.TargetPoint point) {
//        IPacket<?> pkt = message.buildPacket(NetworkDirection.PLAY_TO_CLIENT);
//        getServer().getPlayerList().broadcast(point.);
//        getServer().getPlayerList().sendToAllNearExcept(point.excluded, point.x, point.y, point.z, point.r2,
//                point.world.getDimensionKey(), pkt);
//    }

    @OnlyIn(Dist.CLIENT)
    public void sendToServer(final CustomPacket message) {
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return;
        }
        executor.submit(() -> {
            IPacket<?> packet = message.buildPacket(NetworkDirection.PLAY_TO_SERVER);
            connection.send(packet);
        });
    }

    private void optimized(PacketBuffer buffer, Consumer<PacketBuffer> consumer) {
        if (buffer.readableBytes() < 2000) { // 2k
            consumer.accept(buffer);
            return;
        }
        PacketBuffer newBuffer = new PacketBuffer(buffer.duplicate());
        buffer.retain();
        executor.submit(() -> {
            consumer.accept(newBuffer);
            buffer.release();
        });
    }

    private MinecraftServer getServer() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    public enum PacketTypes {
        PACKET_UPDATE_CONTEXT(UpdateContextPacket.class, UpdateContextPacket::new),

        PACKET_REQUEST_FILE(RequestSkinPacket.class, RequestSkinPacket::new),
        PACKET_RESPONSE_FILE(ResponseSkinPacket.class, ResponseSkinPacket::new),
        PACKET_UPLOAD_FILE(SaveSkinPacket.class, SaveSkinPacket::new),

        PACKET_UPDATE_HOLOGRAM_PROJECTOR(UpdateHologramProjectorPacket.class, UpdateHologramProjectorPacket::new),
        PACKET_UPDATE_COLOUR_MIXER(UpdateColourMixerPacket.class, UpdateColourMixerPacket::new),

        PACKET_UPDATE_LIBRARY_FILE(UpdateLibraryFilePacket.class, UpdateLibraryFilePacket::new),
        PACKET_UPDATE_LIBRARY_FILES(UpdateLibraryFilesPacket.class, UpdateLibraryFilesPacket::new),

        PACKET_OPEN_WARDROBE(OpenWardrobePacket.class, OpenWardrobePacket::new),
        PACKET_UPDATE_WARDROBE(UpdateWardrobePacket.class, UpdateWardrobePacket::new);

        private final Function<PacketBuffer, CustomPacket> factory;

        PacketTypes(Class<? extends CustomPacket> packetClass, Function<PacketBuffer, CustomPacket> factory) {
            this.factory = factory;
            REVERSE_LOOKUP.put(packetClass, this);
        }

        public static PacketTypes getPacket(final int id) {
            return values()[id];
        }

        public static PacketTypes getID(final Class<? extends CustomPacket> c) {
            return REVERSE_LOOKUP.get(c);
        }

        public CustomPacket parsePacket(final PacketBuffer in) throws IllegalArgumentException {
            return this.factory.apply(in);
        }
    }
}
