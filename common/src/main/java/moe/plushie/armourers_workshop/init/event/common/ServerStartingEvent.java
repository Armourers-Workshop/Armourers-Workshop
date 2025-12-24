package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.server.MinecraftServer;

/**
 * Called before {@link ServerStartedEvent}.
 * This event allows for customizations of the server.
 */
public interface ServerStartingEvent {

    MinecraftServer server();
}
