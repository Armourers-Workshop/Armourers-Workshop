package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RequestFilePacket extends CustomPacket {

    private final int MAX_SIZE = 30 * 1024;

    private final int id;
    private final SkinDescriptor descriptor;

    public RequestFilePacket(int id, SkinDescriptor descriptor) {
        this.id = id;
        this.descriptor = descriptor;
    }

    public RequestFilePacket(PacketBuffer buffer) {
        this.id = buffer.readInt();
        this.descriptor = new SkinDescriptor(buffer.readUtf());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(id);
        buffer.writeUtf(descriptor.getIdentifier());
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        AWLog.debug("Process skin request: {}", descriptor);
        AWCore.loader.loadSkinData(descriptor, buffer -> {
            AWLog.debug("Response skin data: {}", descriptor);
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
