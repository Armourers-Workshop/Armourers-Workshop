package moe.plushie.armourers_workshop.core.skin.serializer;

import net.minecraft.server.MinecraftServer;

public enum SkinServerType {
    CLIENT, INTEGRATED_SERVER, DEDICATED_SERVER;

    public static SkinServerType of(MinecraftServer server) {
        if (server == null) {
            return SkinServerType.CLIENT;
        }
        if (server.isDedicatedServer()) {
            return SkinServerType.DEDICATED_SERVER;
        }
        return SkinServerType.INTEGRATED_SERVER;
    }
}
