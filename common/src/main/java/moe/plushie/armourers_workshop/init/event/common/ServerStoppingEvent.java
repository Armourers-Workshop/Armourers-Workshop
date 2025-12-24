package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.server.MinecraftServer;

/**
 * Called when the server begins an orderly shutdown, before {@link ServerStoppedEvent}.
 */
public interface ServerStoppingEvent {

    MinecraftServer server();
}
