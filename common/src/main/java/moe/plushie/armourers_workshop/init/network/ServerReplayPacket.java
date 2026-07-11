package moe.plushie.armourers_workshop.init.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.platform.ReplayManager;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class ServerReplayPacket extends CustomPacket {

    private final Event event;
    private final ByteBuf parameters;

    public ServerReplayPacket(Event event, Consumer<IFriendlyByteBuf> consumer) {
        this.event = event;
        if (consumer != null) {
            this.parameters = Unpooled.buffer();
            consumer.accept(IFriendlyByteBuf.wrap(this.parameters));
        } else {
            this.parameters = null;
        }
    }

    public ServerReplayPacket(IFriendlyByteBuf buffer) {
        this.event = buffer.readEnum(Event.class);
        int size = buffer.readInt();
        if (size != 0) {
            this.parameters = buffer.readBytes(size);
        } else {
            this.parameters = null;
        }
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeEnum(event);
        if (parameters != null) {
            var buf = parameters.slice();
            buffer.writeInt(buf.readableBytes());
            buffer.writeBytes(buf);
        } else {
            buffer.writeInt(0);
        }
    }

    @Override
    public void dispose() {
        Objects.ifPresent(parameters, ByteBuf::release);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        ReplayManager.accept(this);
    }

    public Event event() {
        return event;
    }

    public ByteBuf parameters() {
        return parameters;
    }

    public enum Event {
        START_RECORDING, STOP_RECORDING
    }
}
