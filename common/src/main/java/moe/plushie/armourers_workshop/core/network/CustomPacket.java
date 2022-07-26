package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.other.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.other.network.IServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class CustomPacket {

    /**
     * Sadly {@link FriendlyByteBuf#readUtf()} gets inlined by Proguard which means it's not available on the Server.
     * This field has the default string length that is used for writeString, which then also should be used for
     * readString when it has no special length requirements.
     */
    public static final int MAX_STRING_LENGTH = 32767;

    public static CustomPacket fromBuffer(final FriendlyByteBuf buffer) {
        final int packetType = buffer.readInt();
        return NetworkHandler.PacketTypes.getPacket(packetType).parsePacket(buffer);
    }

    public void accept(final IServerPacketHandler packetHandler, final ServerPlayer player) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a server side handler.");
    }

    public void accept(final IClientPacketHandler packetHandler, final Player player) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a client side handler.");
    }

    public void encode(final FriendlyByteBuf buffer) {
    }

    public int getPacketID() {
        return NetworkHandler.PacketTypes.getID(this.getClass()).ordinal();
    }
}
