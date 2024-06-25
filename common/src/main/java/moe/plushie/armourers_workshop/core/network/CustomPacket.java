package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.function.Function;

public class CustomPacket {

    private static final HashMap<Integer, Function<IFriendlyByteBuf, CustomPacket>> DECODERS = new HashMap<>();
    private static final HashMap<Class<? extends CustomPacket>, Integer> ENCODERS = new HashMap<>();

    public static void register(int id, Class<? extends CustomPacket> clazz, Function<IFriendlyByteBuf, CustomPacket> decoder) {
        ENCODERS.put(clazz, id);
        DECODERS.put(id, decoder);
    }

    public static Function<IFriendlyByteBuf, CustomPacket> getPacketType(int id) {
        return DECODERS.get(id);
    }

    public void accept(final IServerPacketHandler packetHandler, final ServerPlayer player) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a server side handler.");
    }

    public void accept(final IClientPacketHandler packetHandler, final Player player) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a client side handler.");
    }

    public void encode(final IFriendlyByteBuf buffer) {
    }

    public int getPacketID() {
        return ENCODERS.getOrDefault(this.getClass(), -1);
    }
}
