package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.init.common.ModContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class UpdateContextPacket extends CustomPacket {

    public UpdateContextPacket() {
    }

    public UpdateContextPacket(PacketBuffer buffer) {
        ModContext.init(buffer.readUUID(), buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(ModContext.t0());
        buffer.writeUUID(ModContext.t1());
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
    }
}
