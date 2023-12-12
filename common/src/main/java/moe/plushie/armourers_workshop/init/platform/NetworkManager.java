package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.network.CustomReplyPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public class NetworkManager {

    private static Impl IMPL;

    public static void init(String name, String version) {
        IMPL = getInstance(name, version);
    }

    public static void sendToTrackingBlock(final CustomPacket message, final BlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        IMPL.sendToTrackingChunk(message, level.getChunkAt(blockEntity.getBlockPos()));
    }

    public static void sendToTracking(final CustomPacket message, final Entity entity) {
        IMPL.sendToTracking(message, entity);
    }

    public static void sendTo(final CustomPacket message, final ServerPlayer player) {
        IMPL.sendTo(message, player);
    }

    public static void sendToServer(final CustomPacket message) {
        IMPL.sendToServer(message);
    }

    public static void sendToAll(final CustomPacket message) {
        IMPL.sendToAll(message);
    }

    public static void sendWardrobeTo(Entity entity, ServerPlayer player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null) {
            wardrobe.broadcast(player);
        }
    }

    public static <R> void sendTo(final CustomReplyPacket<R> message, final ServerPlayer player, IResultHandler<R> handler) {
        CustomReplyPacket.Receiver.await(message, handler);
        sendTo(message, player);
    }

    public static <R> void sendToServer(final CustomReplyPacket<R> message, IResultHandler<R> handler) {
        CustomReplyPacket.Receiver.await(message, handler);
        sendToServer(message);
    }

    @ExpectPlatform
    public static Impl getInstance(String name, String version) {
        throw new AssertionError();
    }

    public interface Impl {

        void sendToTrackingChunk(final CustomPacket message, final LevelChunk chunk);

        void sendToTracking(final CustomPacket message, final Entity entity);

        void sendTo(final CustomPacket message, final ServerPlayer player);

        void sendToServer(final CustomPacket message);

        void sendToAll(final CustomPacket message);
    }
}

