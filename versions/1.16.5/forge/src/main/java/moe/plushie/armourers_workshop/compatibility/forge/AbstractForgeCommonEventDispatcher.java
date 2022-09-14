package moe.plushie.armourers_workshop.compatibility.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

public abstract class AbstractForgeCommonEventDispatcher {

    public abstract void configDidReload(ForgeConfigSpec spec);

    @SubscribeEvent
    public void _configDidReload(ModConfig.ModConfigEvent event) {
        configDidReload(event.getConfig().getSpec());
    }

    public static abstract class Handler {

        public abstract void serverWillStart(MinecraftServer server);

        public abstract void serverDidStart(MinecraftServer server);

        public abstract void serverWillStop(MinecraftServer server);

        public abstract void serverDidStop(MinecraftServer server);

        @SubscribeEvent
        public void _serverWillStart(FMLServerAboutToStartEvent event) {
            serverWillStart(event.getServer());
        }

        @SubscribeEvent
        public void _serverDidStart(FMLServerStartedEvent event) {
            serverDidStart(event.getServer());
        }

        @SubscribeEvent
        public void _serverWillStop(FMLServerStoppingEvent event) {
            serverWillStop(event.getServer());
        }

        @SubscribeEvent
        public void _serverDidStop(FMLServerStoppedEvent event) {
            serverDidStop(event.getServer());
        }
    }
}
