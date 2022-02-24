package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class ResponseFilePacket extends CustomPacket {

    private final int id;
    private final int offset;
    private final int total;
    private final ByteBuf buffer;

    public ResponseFilePacket(int id, Exception exception) {
        this.id = id;
        this.offset = 0;
        this.total = 0;
        this.buffer = null;
    }

    public ResponseFilePacket(int id, int offset, int total, ByteBuf buffer) {
        this.id = id;
        this.offset = offset;
        this.total = total;
        this.buffer = buffer;
    }

    public ResponseFilePacket(PacketBuffer buffer) {
        this.id = buffer.readInt();
        this.total = buffer.readInt();
        if (this.total != 0) {
            this.offset = buffer.readInt();
            this.buffer = buffer;
            this.buffer.retain();
        } else {
            this.offset = 0;
            this.buffer = null;
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(id);
        buffer.writeInt(total);
        if (this.buffer != null) {
            buffer.writeInt(offset);
            buffer.writeBytes(this.buffer);
        } else {

        }
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        SkinLoader.LoadingTask task = SkinLoader.getInstance().getTask(id);
        if (task != null) {
            task.append(offset, total, buffer);
        }
    }
}
