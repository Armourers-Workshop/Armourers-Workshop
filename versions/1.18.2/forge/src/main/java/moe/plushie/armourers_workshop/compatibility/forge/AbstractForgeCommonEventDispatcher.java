package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public abstract class AbstractForgeCommonEventDispatcher {

    public abstract void configDidReload(ForgeConfigSpec spec);

    @SubscribeEvent
    public void _configDidReload(ModConfigEvent event) {
        ForgeConfigSpec spec = ObjectUtils.safeCast(event.getConfig().getSpec(), ForgeConfigSpec.class);
        if (spec != null) {
            configDidReload(spec);
        }
    }

    public static abstract class Handler {

        public abstract void serverWillStart(MinecraftServer server);

        public abstract void serverDidStart(MinecraftServer server);

        public abstract void serverWillStop(MinecraftServer server);

        public abstract void serverDidStop(MinecraftServer server);

        @SubscribeEvent
        public void _serverWillStart(ServerAboutToStartEvent event) {
            serverWillStart(event.getServer());
        }

        @SubscribeEvent
        public void _serverDidStart(ServerStartedEvent event) {
            serverDidStart(event.getServer());
        }

        @SubscribeEvent
        public void _serverWillStop(ServerStoppingEvent event) {
            serverWillStop(event.getServer());
        }

        @SubscribeEvent
        public void _serverDidStop(ServerStoppedEvent event) {
            serverDidStop(event.getServer());
        }
    }
}
