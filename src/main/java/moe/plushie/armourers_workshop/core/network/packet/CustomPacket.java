package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraftforge.fml.network.NetworkDirection;
import org.apache.commons.lang3.tuple.Pair;

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

    public IPacket<?> buildPacket(final NetworkDirection direction) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeInt(getPacketID());
        encode(buffer);
        buffer.capacity(buffer.readableBytes());
        if (buffer.readableBytes() > 2 * 1024 * 1024) { // 2k walking room :)
            throw new IllegalArgumentException("Sorry AW2 made a " + buffer.readableBytes() + " byte packet by accident!");
        }
        return direction.buildPacket(Pair.of(buffer, 0), NetworkHandler.getInstance().getChannel()).getThis();
    }

    private int getPacketID() {
        return NetworkHandler.PacketTypes.getID(this.getClass()).ordinal();
    }
}
