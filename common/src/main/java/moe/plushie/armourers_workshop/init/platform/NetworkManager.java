package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.network.UpdateContextPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class NetworkManager {

    @ExpectPlatform
    public static void init(String name, String version) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToAll(final CustomPacket message) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToTracking(final CustomPacket message, final Entity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendTo(final CustomPacket message, final ServerPlayer player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Environment(value = EnvType.CLIENT)
    public static void sendToServer(final CustomPacket message) {
        throw new AssertionError();
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
}

