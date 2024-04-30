package moe.plushie.armourers_workshop.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

public class PacketSplitter {

    private final int SPLIT_BODY_FLAG = -1;
    private final int SPLIT_BEGIN_FLAG = -2;
    private final int SPLIT_END_FLAG = -3;

    private final HashMap<UUID, ArrayList<ByteBuf>> receivedBuffers = new HashMap<>();
    private final ExecutorService workThread = ThreadUtils.newFixedThreadPool(2, "AW-NET/N-ED");

    public PacketSplitter() {
    }

    public <T> void split(final CustomPacket message, Function<IFriendlyByteBuf, T> builder, int partSize, Consumer<T> consumer) {
        workThread.submit(() -> {
            ByteBuf buffer = Unpooled.buffer();
            writePacket(message, buffer);
            buffer.capacity(buffer.readableBytes());
            // when packet exceeds the part size, it will be split automatically
            int bufferSize = buffer.readableBytes();
            if (bufferSize <= partSize) {
                T packet = builder.apply(IFriendlyByteBuf.wrap(buffer));
                consumer.accept(packet);
                return;
            }
            for (int index = 0; index < bufferSize; index += partSize) {
                ByteBuf partPrefix = Unpooled.buffer(4);
                if (index == 0) {
                    partPrefix.writeInt(SPLIT_BEGIN_FLAG);
                } else if ((index + partSize) >= bufferSize) {
                    partPrefix.writeInt(SPLIT_END_FLAG);
                } else {
                    partPrefix.writeInt(SPLIT_BODY_FLAG);
                }
                int resolvedPartSize = Math.min(bufferSize - index, partSize);
                ByteBuf buffer1 = Unpooled.wrappedBuffer(partPrefix, buffer.retainedSlice(buffer.readerIndex(), resolvedPartSize));
                buffer.skipBytes(resolvedPartSize);
                T packet = builder.apply(IFriendlyByteBuf.wrap(buffer1));
                consumer.accept(packet);
            }
            buffer.release();
        });
    }

    public void merge(UUID uuid, IFriendlyByteBuf bufferIn, Consumer<CustomPacket> consumer) {
        ByteBuf buffer = bufferIn.asByteBuf();
        int packetState = buffer.getInt(0);
        if (packetState < 0) {
            ArrayList<ByteBuf> playerReceivedBuffers = receivedBuffers.computeIfAbsent(uuid, k -> new ArrayList<>());
            if (packetState == SPLIT_BEGIN_FLAG) {
                if (!playerReceivedBuffers.isEmpty()) {
                    ModLog.warn("aw2:split received out of order - inbound buffer not empty when receiving first");
                    playerReceivedBuffers.clear();
                }
            }
            buffer.skipBytes(4); // skip header
            playerReceivedBuffers.add(buffer.retainedDuplicate()); // we need to keep writer/reader index
            if (packetState == SPLIT_END_FLAG) {
                workThread.submit(() -> {
                    // ownership will transfer to full buffer, so don't call release again.
                    ByteBuf full = Unpooled.wrappedBuffer(playerReceivedBuffers.toArray(new ByteBuf[0]));
                    playerReceivedBuffers.clear();
                    consumer.accept(readPacket(full));
                    full.release();
                });
            }
            return;
        }
        if (buffer.readableBytes() < 3000) { // 3k
            consumer.accept(readPacket(buffer));
            return;
        }
        ByteBuf receivedBuf = buffer.retainedDuplicate(); // we need to keep writer/reader index
        workThread.submit(() -> {
            consumer.accept(readPacket(receivedBuf));
            receivedBuf.release();
        });
    }

    private void writePacket(CustomPacket message, ByteBuf buffer) {
        buffer.writeInt(message.getPacketID());
        message.encode(IFriendlyByteBuf.wrap(buffer));
    }

    private CustomPacket readPacket(ByteBuf buffer) {
        int packetId = buffer.readInt();
        Function<IFriendlyByteBuf, CustomPacket> decoder = CustomPacket.getPacketType(packetId);
        if (decoder != null) {
            return decoder.apply(IFriendlyByteBuf.wrap(buffer));
        }
        throw new UnsupportedOperationException("This packet ( " + packetId + ") does not support in this version.");
    }
}
