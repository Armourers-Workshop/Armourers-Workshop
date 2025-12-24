package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.server.MinecraftServer;

/**
 * Called after {@link ServerStoppingEvent} when the server has completely shut down.
 * Called immediately before shutting down, on the dedicated server, and before returning
 * to the main menu on the client.
 */
public interface ServerStoppedEvent {

    MinecraftServer server();
}
