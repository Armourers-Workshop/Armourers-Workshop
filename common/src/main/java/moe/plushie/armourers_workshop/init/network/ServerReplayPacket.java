package moe.plushie.armourers_workshop.init.network;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.platform.ReplayManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class ServerReplayPacket extends CustomPacket {

    private final Event event;
    private final FriendlyByteBuf parameters;

    public ServerReplayPacket(Event event, Consumer<FriendlyByteBuf> consumer) {
        this.event = event;
        if (consumer != null) {
            this.parameters = new FriendlyByteBuf(Unpooled.buffer());
            consumer.accept(this.parameters);
        } else {
            this.parameters = null;
        }
    }

    public ServerReplayPacket(FriendlyByteBuf buffer) {
        this.event = buffer.readEnum(Event.class);
        int size = buffer.readInt();
        if (size != 0) {
            this.parameters = new FriendlyByteBuf(buffer.readBytes(size));
        } else {
            this.parameters = null;
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(event);
        if (parameters != null) {
            buffer.writeInt(parameters.readableBytes());
            buffer.writeBytes(parameters);
        } else {
            buffer.writeInt(0);
        }
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        ReplayManager.accept(this);
    }

    public Event getEvent() {
        return event;
    }

    public FriendlyByteBuf getParameters() {
        return parameters;
    }

    public enum Event {
        START_RECORDING, STOP_RECORDING
    }
}
