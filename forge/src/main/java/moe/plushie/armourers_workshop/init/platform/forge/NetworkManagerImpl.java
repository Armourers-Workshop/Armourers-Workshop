package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeNetworkManager;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.PacketSplitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class NetworkManagerImpl extends AbstractForgeNetworkManager implements NetworkManager.Impl {

    private static NetworkDispatcher dispatcher;

    public static NetworkManager.Impl getInstance(String name, String version) {
        NetworkManagerImpl impl = new NetworkManagerImpl();
        impl.init(name, version);
        return impl;
    }

    public void init(String name, String version) {
        dispatcher = new NetworkDispatcher(ModConstants.key(name));
        AbstractForgeNetworkManager.register(dispatcher.channelName, version, dispatcher);
    }

    @Override
    public void sendToTracking(final CustomPacket message, final Entity entity) {
        dispatcher.split(message, Direction.PLAY_TO_CLIENT, AbstractForgeNetworkManager.trackingEntityAndSelf(() -> entity)::send);
    }

    @Override
    public void sendTo(final CustomPacket message, final ServerPlayer player) {
        dispatcher.split(message, Direction.PLAY_TO_CLIENT, player.connection::send);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void sendToServer(final CustomPacket message) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            dispatcher.split(message, Direction.PLAY_TO_SERVER, connection::send);
        }
    }

    @Override
    public void sendToAll(CustomPacket message) {
        dispatcher.split(message, Direction.PLAY_TO_CLIENT, AbstractForgeNetworkManager.allPlayers()::send);
    }

    public static class NetworkDispatcher implements IServerPacketHandler, IClientPacketHandler {

        final UUID clientUUID = UUID.randomUUID();
        final ResourceLocation channelName;
        final PacketSplitter splitter;

        final int maxPartSize = 32000; // 32k

        NetworkDispatcher(ResourceLocation channelName) {
            this.channelName = channelName;
            this.splitter = new PacketSplitter();
        }

        @SubscribeEvent
        public void onServerEvent(final ClientCustomPayloadEvent event) {
            Context context = event.getSource().get();
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            IServerPacketHandler packetHandler = this;
            merge(player.getUUID(), event.getPayload(), packet -> context.enqueueWork(() -> packet.accept(packetHandler, player)));
            context.setPacketHandled(true);
        }

        @OnlyIn(value = Dist.CLIENT)
        @SubscribeEvent
        public void onClientEvent(final ServerCustomPayloadEvent event) {
            if (event instanceof ServerCustomPayloadLoginEvent) {
                return;
            }
            IClientPacketHandler packetHandler = this;
            Context context = event.getSource().get();
            merge(clientUUID, event.getPayload(), packet -> context.enqueueWork(() -> packet.accept(packetHandler, Minecraft.getInstance().player)));
            context.setPacketHandled(true);
        }

        public void merge(UUID uuid, FriendlyByteBuf buffer, Consumer<CustomPacket> consumer) {
            splitter.merge(uuid, buffer, consumer);
        }

        public void split(final CustomPacket message, Direction dir, Consumer<Packet<?>> consumer) {
            int partSize = maxPartSize;
            // download from the server side, the forge is resolved, the maximum packet size is than 10m.
            if (dir == Direction.PLAY_TO_CLIENT) {
                partSize = Integer.MAX_VALUE;
            }
            splitter.split(message, buf -> dir.buildPacket(Pair.of(buf, 0), channelName).getThis(), partSize, consumer);
        }
    }
}
