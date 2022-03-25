package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RequestFilePacket extends CustomPacket {

    public final int MAX_SIZE = 30 * 1024; // 30k

    private final int id;
    private final String resource;

    public RequestFilePacket(int id, String resource) {
        this.id = id;
        this.resource = resource;
    }

    public RequestFilePacket(PacketBuffer buffer) {
        this.id = buffer.readInt();
        this.resource = buffer.readUtf();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(id);
        buffer.writeUtf(resource);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        AWLog.debug("Process skin request: {}", resource);
        DataManager.getInstance().loadSkinData(resource, buffer -> {
            AWLog.debug("Response skin data: {}", resource);
            for (CustomPacket packet : buildResponsePacket(buffer.orElse(null))) {
                NetworkHandler.getInstance().sendTo(packet, player);
            }
        });
    }

    private ArrayList<CustomPacket> buildResponsePacket(@Nullable ByteBuf buffer) {
        ArrayList<CustomPacket> packets = new ArrayList<>();
        if (buffer == null || buffer.readableBytes() == 0) {
            packets.add(new ResponseFilePacket(id, new FileNotFoundException("can't found file")));
            return packets;
        }
        int total = buffer.readableBytes();
        int offset = 0;
        while (offset < total) {
            int length = Math.min(total - offset, MAX_SIZE);
            ByteBuf buffer1 = buffer.copy(offset, length);
            packets.add(new ResponseFilePacket(id, offset, total, buffer1));
            offset += length;
        }
        return packets;
    }
}
