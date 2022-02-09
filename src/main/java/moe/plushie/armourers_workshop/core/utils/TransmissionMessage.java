package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class TransmissionMessage {

    private final static ArrayList<PacketBufferCoder<?>> CODERS = new ArrayList<>();

    private final static HashMap<PacketBufferCoder<?>, BiConsumer<TransmissionMessage, NetworkEvent.Context>> REQUEST_HANDLERS = new HashMap<>();
    private final static HashMap<PacketBufferCoder<?>, BiConsumer<TransmissionMessage, NetworkEvent.Context>> RESPONSE_HANDLERS = new HashMap<>();


    final int id;
    final int type;
    final LazyOptional<Object> value;

    public TransmissionMessage(int id, Object value) {
        this.id = id;
        this.type = getTypeId(value.getClass());
        this.value = LazyOptional.of(() -> value);
    }

    public TransmissionMessage(PacketBuffer buffer) {
        this.id = buffer.readInt();
        this.type = buffer.readInt();
        // create a lazy context
        PacketBufferCoder<Object> coder = getCoder();
        if (coder == null) {
            this.value = LazyOptional.empty();
            return;
        }
        this.value = LazyOptional.of(() -> coder.decoder.apply(buffer));
        this.value.addListener(o -> buffer.release());
        buffer.retain();
    }

    public static <Request, Response> void requestHandler(PacketBufferCoder<Request> requestCoder, PacketBufferCoder<Response> responseCoder, BiConsumer<TransmissionMessage, NetworkEvent.Context> consumer) {
        if (!CODERS.contains(requestCoder)) {
            CODERS.add(requestCoder);
        }
        REQUEST_HANDLERS.put(requestCoder, consumer);
    }

    public static <Request, Response> void responseHandler(PacketBufferCoder<Request> requestCoder, PacketBufferCoder<Response> responseCoder, BiConsumer<TransmissionMessage, NetworkEvent.Context> consumer) {
        if (!CODERS.contains(responseCoder)) {
            CODERS.add(responseCoder);
        }
        RESPONSE_HANDLERS.put(responseCoder, consumer);
    }

    public static int getTypeId(Class<?> requestClass) {
        for (int i = 0; i < CODERS.size(); ++i) {
            if (CODERS.get(i).type == requestClass) {
                return i;
            }
        }
        return -1;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeInt(id);
        buffer.writeInt(type);
        PacketBufferCoder<Object> coder = getCoder();
        if (coder == null || !value.isPresent()) {
            return;
        }
        coder.encoder.accept(value.resolve().orElse(null), buffer);
    }

    public void execute(Supplier<NetworkEvent.Context> context) {
//            T value = this.value.resolve().orElse(null);
//            getCoder().execute(context, value);

        // complete
        this.value.invalidate();
        context.get().setPacketHandled(true);
    }

    public <Response> void reply(Response response, NetworkEvent.Context context, SimpleChannel channel) {
        TransmissionMessage message = new TransmissionMessage(id, response);
        channel.reply(message, context);
    }

    public int getId() {
        return id;
    }

    @Nullable
    public <T> T getValue() {
        LazyOptional<T> bridgeValue = value.cast();
        return bridgeValue.resolve().orElse(null);
    }

    @Nullable
    public PacketBufferCoder<Object> getCoder() {
        if (type < 0 || type >= CODERS.size()) {
            return null;
        }
        return (PacketBufferCoder<Object>) CODERS.get(type);
    }
}
