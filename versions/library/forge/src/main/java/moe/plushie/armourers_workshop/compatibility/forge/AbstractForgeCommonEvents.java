package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeBlockEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeConfigEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeLauncherLifecycleEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgePlayerEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeRegisterCommandsEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeRegisterDataPackEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeRegisterEntityAttributesEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeServerLevelEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.common.AbstractForgeServerLifecycleEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.common.BlockEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherClientSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherConfigSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherLoadCompleteEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.PlayerEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterCommandsEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterDataPackEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterEntityAttributesEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerLevelAddEntityEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerLevelTickEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartedEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppingEvent;

public class AbstractForgeCommonEvents extends AbstractForgeCommonEventsImpl {

    public static void init() {
        EventManager.post(LauncherConfigSetupEvent.class, AbstractForgeConfigEvent.registryFactory());

        EventManager.post(LauncherClientSetupEvent.class, AbstractForgeLauncherLifecycleEvent.clientSetupFactory());
        EventManager.post(LauncherCommonSetupEvent.class, AbstractForgeLauncherLifecycleEvent.commonSetupFactory());
        EventManager.post(LauncherLoadCompleteEvent.class, AbstractForgeLauncherLifecycleEvent.loadCompleteFactory());

        EventManager.post(ServerStartingEvent.class, AbstractForgeServerLifecycleEvent.aboutToStartFactory());
        EventManager.post(ServerStartedEvent.class, AbstractForgeServerLifecycleEvent.startedFactory());
        EventManager.post(ServerStoppingEvent.class, AbstractForgeServerLifecycleEvent.stoppingFactory());
        EventManager.post(ServerStoppedEvent.class, AbstractForgeServerLifecycleEvent.stoppedFactory());

        EventManager.post(ServerLevelTickEvent.Pre.class, AbstractForgeServerLevelEvent.preTickFactory());
        EventManager.post(ServerLevelTickEvent.Post.class, AbstractForgeServerLevelEvent.postTickFactory());

        EventManager.post(ServerLevelAddEntityEvent.class, AbstractForgeServerLevelEvent.addEntityFactory());

        EventManager.post(BlockEvent.Break.class, AbstractForgeBlockEvent.breakFactory());
        EventManager.post(BlockEvent.Place.class, AbstractForgeBlockEvent.placeFactory());

        EventManager.post(PlayerEvent.LoggingIn.class, AbstractForgePlayerEvent.loggingInFactory());
        EventManager.post(PlayerEvent.LoggingOut.class, AbstractForgePlayerEvent.loggingOutFactory());
        EventManager.post(PlayerEvent.Death.class, AbstractForgePlayerEvent.deathFactory());
        EventManager.post(PlayerEvent.Clone.class, AbstractForgePlayerEvent.cloneFactory());
        EventManager.post(PlayerEvent.Attack.class, AbstractForgePlayerEvent.attackFactory());
        EventManager.post(PlayerEvent.StartTracking.class, AbstractForgePlayerEvent.startTrackingFactory());

        EventManager.post(RegisterCommandsEvent.class, AbstractForgeRegisterCommandsEvent.registryFactory());
        EventManager.post(RegisterDataPackEvent.class, AbstractForgeRegisterDataPackEvent.registryFactory());
        EventManager.post(RegisterEntityAttributesEvent.class, AbstractForgeRegisterEntityAttributesEvent.registryFactory());
    }
}
