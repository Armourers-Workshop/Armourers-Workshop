package moe.plushie.armourers_workshop.init.platform.event.common;

import net.minecraft.server.MinecraftServer;

public interface ServerStoppedEvent {

    MinecraftServer getServer();
}
