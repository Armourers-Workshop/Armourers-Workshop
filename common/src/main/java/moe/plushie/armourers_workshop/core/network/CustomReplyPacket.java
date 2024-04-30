package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomReplyPacket<R> extends CustomPacket {

    private final int id;
    private final IEntitySerializer<R> serializer;

    public CustomReplyPacket(IEntitySerializer<R> serializer) {
        this.id = Receiver.COUNTER.getAndIncrement();
        this.serializer = serializer;
    }

    public CustomReplyPacket(IEntitySerializer<R> serializer, IFriendlyByteBuf buffer) {
        this.id = buffer.readInt();
        this.serializer = serializer;
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }

    public void accept(final IServerPacketHandler packetHandler, final ServerPlayer player, final IResultHandler<R> reply) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a server side handler.");
    }

    public void accept(final IClientPacketHandler packetHandler, final Player player, final IResultHandler<R> reply) {
        throw new UnsupportedOperationException("This packet ( " + this.getPacketID() + ") does not implement a client side handler.");
    }

    @Override
    public final void accept(final IServerPacketHandler packetHandler, final ServerPlayer player) {
        accept(packetHandler, player, (result, exception) -> NetworkManager.sendTo(new Receiver<>(id, result, exception, this), player));
    }

    @Override
    public final void accept(final IClientPacketHandler packetHandler, final Player player) {
        accept(packetHandler, player, (result, exception) -> NetworkManager.sendToServer(new Receiver<>(id, result, exception, this)));
    }

    public static class Request<R> {

        public final IEntitySerializer<R> serializer;
        public final IResultHandler<R> handler;

        public Request(CustomReplyPacket<R> packet, IResultHandler<R> handler) {
            this.handler = handler;
            this.serializer = Optional.of(packet).map(f -> f.serializer).orElse(null);
        }

        public static <R> R read(Request<R> request, IFriendlyByteBuf buf) {
            if (request != null && request.serializer != null) {
                return request.serializer.read(buf);
            }
            return null;
        }

        public static <R> void write(Request<R> request, IFriendlyByteBuf buf, R result) {
            if (request != null && request.serializer != null) {
                request.serializer.write(buf, result);
            }
        }
    }

    public static class Receiver<R> extends CustomPacket {

        private static final AtomicInteger COUNTER = new AtomicInteger(1000);
        private static final ScheduledExecutorService TIMER = ThreadUtils.newSingleThreadScheduledExecutor();
        private static final HashMap<Integer, Request<?>> REQUESTS = new HashMap<>();

        private final int id;
        private final R result;
        private final Exception exception;

        private final Request<R> request;

        public Receiver(int id, R result, Exception exception, CustomReplyPacket<R> packet) {
            this.id = id;
            this.result = result;
            this.exception = exception;
            this.request = new Request<>(packet, null);
        }

        public Receiver(IFriendlyByteBuf buffer) {
            this.id = buffer.readInt();
            this.request = popPendingRequest();
            if (buffer.readBoolean()) {
                // request executed successfully.
                this.result = Request.read(request, buffer);
                this.exception = null;
            } else {
                // request execution failed.
                this.result = null;
                this.exception = DataSerializers.EXCEPTION.read(buffer);
            }
        }

        public static <R> void await(CustomReplyPacket<R> packet, IResultHandler<R> handler) {
            int id = packet.id;
            int timeout = 30;
            REQUESTS.put(id, new Request<>(packet, handler));
            TIMER.scheduleAtFixedRate(() -> {
                Request<?> request = REQUESTS.remove(id);
                if (request != null && request.handler != null) {
                    request.handler.throwing(new RuntimeException("Request timeout"));
                }
            }, timeout, timeout, TimeUnit.SECONDS);
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
            buffer.writeInt(id);
            if (result != null) {
                // request executed successfully.
                buffer.writeBoolean(true);
                Request.write(request, buffer, result);
            } else {
                // request execution failed.
                buffer.writeBoolean(false);
                DataSerializers.EXCEPTION.write(buffer, exception);
            }
        }

        @Override
        public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
            if (request != null && request.handler != null) {
                request.handler.apply(result, exception);
            }
        }

        @Override
        public void accept(IClientPacketHandler packetHandler, Player player) {
            if (request != null && request.handler != null) {
                request.handler.apply(result, exception);
            }
        }

        private Request<R> popPendingRequest() {
            Request<?> req = REQUESTS.remove(id);
            if (req != null) {
                return ObjectUtils.unsafeCast(req);
            }
            return null;
        }
    }

    static {
        CustomPacket.register(Integer.MAX_VALUE, Receiver.class, Receiver::new);
    }
}
