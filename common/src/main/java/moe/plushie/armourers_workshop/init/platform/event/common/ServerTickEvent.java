package moe.plushie.armourers_workshop.init.platform.event.common;

import net.minecraft.server.MinecraftServer;

public interface ServerTickEvent {

    MinecraftServer getServer();

    interface Pre extends ServerTickEvent {
    }

    interface Post extends ServerTickEvent {
    }
}
