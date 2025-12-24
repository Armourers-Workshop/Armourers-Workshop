package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.server.MinecraftServer;

public interface ServerTickEvent {

    MinecraftServer server();

    interface Pre extends ServerTickEvent {
    }

    interface Post extends ServerTickEvent {
    }
}
