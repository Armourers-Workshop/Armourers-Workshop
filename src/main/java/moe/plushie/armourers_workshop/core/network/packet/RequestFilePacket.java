package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RequestFilePacket extends CustomPacket {

    public final int MAX_SIZE = 30 * 1024; // 30k

    private final int seq;
    private final String identifier;

    public RequestFilePacket(int seq, String identifier) {
        this.seq = seq;
        this.identifier = identifier;
    }

    public RequestFilePacket(PacketBuffer buffer) {
        this.seq = buffer.readInt();
        this.identifier = buffer.readUtf();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(seq);
        buffer.writeUtf(identifier);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        ModLog.debug("Process skin request: {}", identifier);
        DataManager.getInstance().loadSkinData(identifier, buffer -> {
            ModLog.debug("Response skin data: {}", identifier);
            for (CustomPacket packet : buildResponsePacket(buffer.orElse(null))) {
                NetworkHandler.getInstance().sendTo(packet, player);
            }
        });
    }

    private ArrayList<CustomPacket> buildResponsePacket(@Nullable ByteBuf buffer) {
        ArrayList<CustomPacket> packets = new ArrayList<>();
        if (buffer == null || buffer.readableBytes() == 0) {
            packets.add(new ResponseFilePacket(seq, new FileNotFoundException("can't found file")));
            return packets;
        }
        int total = buffer.readableBytes();
        int offset = 0;
        while (offset < total) {
            int length = Math.min(total - offset, MAX_SIZE);
            ByteBuf buffer1 = buffer.copy(offset, length);
            packets.add(new ResponseFilePacket(seq, offset, total, buffer1));
            offset += length;
        }
        return packets;
    }
}
