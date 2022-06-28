package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

public class CustomPacket {

    /**
     * Sadly {@link PacketBuffer#readString()} gets inlined by Proguard which means it's not available on the Server.
     * This field has the default string length that is used for writeString, which then also should be used for
     * readString when it has no special length requirements.
     */
    public static final int MAX_STRING_LENGTH = 32767;

    public static CustomPacket fromBuffer(final PacketBuffer buffer) {
        final int packetType = buffer.readInt();
        return NetworkHandler.PacketTypes.getPacket(packetType).parsePacket(buffer);
    }

    public void accept(final ServerPlayNetHandler netHandler, final ServerPlayerEntity player) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a server side handler.");
    }

    public void accept(final INetHandler netHandler, final PlayerEntity player) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a client side handler.");
    }

    public void encode(final PacketBuffer buffer) {
    }

    public int getPacketID() {
        return NetworkHandler.PacketTypes.getID(this.getClass()).ordinal();
    }
}
