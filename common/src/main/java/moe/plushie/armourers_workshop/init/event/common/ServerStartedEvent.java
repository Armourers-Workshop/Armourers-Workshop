package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.server.MinecraftServer;

/**
 * Called after {@link ServerStartingEvent} when the server is available and ready to play.
 */
public interface ServerStartedEvent {

    MinecraftServer server();
}
