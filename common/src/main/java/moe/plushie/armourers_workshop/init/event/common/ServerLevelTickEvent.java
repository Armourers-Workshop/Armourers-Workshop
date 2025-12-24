package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.server.level.ServerLevel;

public interface ServerLevelTickEvent {

    ServerLevel level();

    interface Pre extends ServerLevelTickEvent {
    }

    interface Post extends ServerLevelTickEvent {
    }
}
