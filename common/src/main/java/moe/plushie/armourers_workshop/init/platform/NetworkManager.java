package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.network.UpdateContextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class NetworkManager {

    private static Impl IMPL;

    public static void init(String name, String version) {
        IMPL = getInstance(name, version);
    }

    public static void sendToAll(final CustomPacket message) {
        IMPL.sendToAll(message);
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

    public static void sendContextToAll() {
        sendToAll(new UpdateContextPacket());
    }

    public static void sendContextTo(ServerPlayer player) {
        sendTo(new UpdateContextPacket(), player);
    }

    public static void sendWardrobeTo(Entity entity, ServerPlayer player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null) {
            wardrobe.broadcast(player);
        }
    }

    @ExpectPlatform
    public static Impl getInstance(String name, String version) {
        throw new AssertionError();
    }

    public interface Impl {

        void sendToAll(final CustomPacket message);

        void sendToTracking(final CustomPacket message, final Entity entity);

        void sendTo(final CustomPacket message, final ServerPlayer player);

        void sendToServer(final CustomPacket message);
    }
}

