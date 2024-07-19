package moe.plushie.armourers_workshop.init.platform.event.common;

import net.minecraft.server.level.ServerLevel;

public interface ServerLevelTickEvent {

    ServerLevel getLevel();

    interface Pre extends ServerLevelTickEvent {
    }

    interface Post extends ServerLevelTickEvent {
    }
}
