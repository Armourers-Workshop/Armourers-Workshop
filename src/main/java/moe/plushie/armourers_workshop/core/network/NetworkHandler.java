package moe.plushie.armourers_workshop.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.network.packet.*;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
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
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class NetworkHandler {

    private static final byte STATE_FIRST = -2;
    private static final byte STATE_LAST = -3;
    private static final int MAX_PART_SIZE = 32000; // 32k

    private static final HashMap<Class<? extends CustomPacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<>();
    private static NetworkHandler INSTANCE;

    private final ResourceLocation channelName;
    private final HashMap<UUID, ArrayList<ByteBuf>> receivedBuffers = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(2, r -> new Thread(r, "Network-Data-Coder"));

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
        PlayerEntity player = context.getSender();
        if (player == null) {
            return;
        }
        ServerPlayNetHandler netHandler = (ServerPlayNetHandler) context.getNetworkManager().getPacketListener();
        receiveSplitPacket(player.getUUID(), event.getPayload(), payload -> {
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
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        NetworkEvent.Context context = event.getSource().get();
        ClientPlayNetHandler netHandler = (ClientPlayNetHandler) context.getNetworkManager().getPacketListener();
        receiveSplitPacket(player.getUUID(), event.getPayload(), payload -> {
            CustomPacket packet = CustomPacket.fromBuffer(payload);
            context.enqueueWork(() -> packet.accept(netHandler, player));
        });
        context.setPacketHandled(true);
    }

    public ResourceLocation getChannel() {
        return this.channelName;
    }

    public void sendToAll(final CustomPacket message) {
        sendSplitPacket(message, NetworkDirection.PLAY_TO_CLIENT, getServer().getPlayerList()::broadcastAll);
    }

    public void sendTo(final CustomPacket message, final ServerPlayerEntity player) {
        sendSplitPacket(message, NetworkDirection.PLAY_TO_CLIENT, player.connection::send);
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
        if (connection != null) {
            sendSplitPacket(message, NetworkDirection.PLAY_TO_SERVER, connection::send);
        }
    }

    private void sendSplitPacket(final CustomPacket message, final NetworkDirection direction, Consumer<IPacket<?>> consumer) {
        executor.submit(() -> {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeInt(message.getPacketID());
            message.encode(buffer);
            buffer.capacity(buffer.readableBytes());
            // when packet exceeds the part size, it will be split automatically
            int packetSize = buffer.readableBytes();
            if (direction == NetworkDirection.PLAY_TO_CLIENT || packetSize <= MAX_PART_SIZE) {
                IPacket<?> packet = direction.buildPacket(Pair.of(buffer, 0), getChannel()).getThis();
                consumer.accept(packet);
                return;
            }
            for (int index = 0; index < packetSize; index += MAX_PART_SIZE) {
                ByteBuf partPrefix = Unpooled.buffer(4);
                if (index == 0) {
                    partPrefix.writeInt(STATE_FIRST);
                } else if ((index + MAX_PART_SIZE) >= packetSize) {
                    partPrefix.writeInt(STATE_LAST);
                } else {
                    partPrefix.writeInt(-1);
                }
                int partSize = Math.min(MAX_PART_SIZE, packetSize - index);
                ByteBuf buffer1 = Unpooled.wrappedBuffer(partPrefix, buffer.retainedSlice(buffer.readerIndex(), partSize));
                buffer.skipBytes(partSize);
                IPacket<?> packet = direction.buildPacket(Pair.of(new PacketBuffer(buffer1), 0), getChannel()).getThis();
                consumer.accept(packet);
            }
            buffer.release();
        });
    }

    private void receiveSplitPacket(UUID uuid, PacketBuffer buffer, Consumer<PacketBuffer> consumer) {
        int packetState = buffer.getInt(0);
        if (packetState < 0) {
            ArrayList<ByteBuf> playerReceivedBuffers = receivedBuffers.computeIfAbsent(uuid, k -> new ArrayList<>());
            if (packetState == STATE_FIRST) {
                if (!playerReceivedBuffers.isEmpty()) {
                    ModLog.warn("aw2:split received out of order - inbound buffer not empty when receiving first");
                    playerReceivedBuffers.clear();
                }
            }
            buffer.skipBytes(4); // skip header
            playerReceivedBuffers.add(buffer.retainedDuplicate()); // we need to keep writer/reader index
            if (packetState == STATE_LAST) {
                executor.submit(() -> {
                    // ownership will transfer to full buffer, so don't call release again.
                    PacketBuffer full = new PacketBuffer(Unpooled.wrappedBuffer(playerReceivedBuffers.toArray(new ByteBuf[0])));
                    playerReceivedBuffers.clear();
                    consumer.accept(full);
                    full.release();
                });
            }
            return;
        }
        if (buffer.readableBytes() < 3000) { // 3k
            consumer.accept(buffer);
            return;
        }
        ByteBuf receivedBuf = buffer.retainedDuplicate(); // we need to keep writer/reader index
        executor.submit(() -> {
            consumer.accept(new PacketBuffer(receivedBuf));
            receivedBuf.release();
        });
    }

    private MinecraftServer getServer() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    public enum PacketTypes {
        PACKET_UPDATE_CONTEXT(UpdateContextPacket.class, UpdateContextPacket::new),

        PACKET_UNDO_ACTION(UndoActionPacket.class, UndoActionPacket::new),
        PACKET_UPDATE_COLOR_PICKER(UpdateColorPickerPacket.class, UpdateColorPickerPacket::new),

        PACKET_REQUEST_FILE(RequestSkinPacket.class, RequestSkinPacket::new),
        PACKET_RESPONSE_FILE(ResponseSkinPacket.class, ResponseSkinPacket::new),
        PACKET_UPLOAD_FILE(SaveSkinPacket.class, SaveSkinPacket::new),

        PACKET_UPLOAD_SKIN_TO_GLOBAL(UploadSkinPacket.class, UploadSkinPacket::new),
        PACKET_UPDATE_OUTFIT_MAKER(UpdateOutfitMakerPacket.class, UpdateOutfitMakerPacket::new),
        PACKET_UPDATE_ARMOURER(UpdateArmourerPacket.class, UpdateArmourerPacket::new),

        PACKET_UPDATE_HOLOGRAM_PROJECTOR(UpdateHologramProjectorPacket.class, UpdateHologramProjectorPacket::new),
        PACKET_UPDATE_COLOUR_MIXER(UpdateColorMixerPacket.class, UpdateColorMixerPacket::new),

        PACKET_UPDATE_PAINTING_TOOL(UpdatePaintingToolPacket.class, UpdatePaintingToolPacket::new),
        PACKET_UPDATE_BLOCK_COLOR(UpdateBlockColorPacket.class, UpdateBlockColorPacket::new),

        PACKET_UPDATE_LIBRARY_FILE(UpdateLibraryFilePacket.class, UpdateLibraryFilePacket::new),
        PACKET_UPDATE_LIBRARY_FILES(UpdateLibraryFilesPacket.class, UpdateLibraryFilesPacket::new),

        PACKET_EXECUTE_COMMAND(ExecuteCommandPacket.class, ExecuteCommandPacket::new),

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
