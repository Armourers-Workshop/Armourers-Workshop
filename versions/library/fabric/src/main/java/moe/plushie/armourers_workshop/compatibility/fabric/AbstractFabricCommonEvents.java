package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricBlockEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricConfigEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricLauncherLifecycleEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricPlayerEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricRegisterCommandsEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricRegisterDataPackEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricRegisterEntityAttributesEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricServerLevelEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricServerLifecycleEvent;
import moe.plushie.armourers_workshop.compatibility.fabric.event.common.AbstractFabricServerTickEvent;
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
import moe.plushie.armourers_workshop.init.platform.event.common.ServerTickEvent;

public class AbstractFabricCommonEvents {

    public static void init() {
        EventManager.post(LauncherConfigSetupEvent.class, AbstractFabricConfigEvent.registryFactory());

        EventManager.post(LauncherClientSetupEvent.class, AbstractFabricLauncherLifecycleEvent.clientSetupFactory());
        EventManager.post(LauncherCommonSetupEvent.class, AbstractFabricLauncherLifecycleEvent.commonSetupFactory());
        EventManager.post(LauncherLoadCompleteEvent.class, AbstractFabricLauncherLifecycleEvent.loadCompleteFactory());

        EventManager.post(ServerStartingEvent.class, AbstractFabricServerLifecycleEvent.aboutToStartFactory());
        EventManager.post(ServerStartedEvent.class, AbstractFabricServerLifecycleEvent.startedFactory());
        EventManager.post(ServerStoppingEvent.class, AbstractFabricServerLifecycleEvent.stoppingFactory());
        EventManager.post(ServerStoppedEvent.class, AbstractFabricServerLifecycleEvent.stoppedFactory());

        EventManager.post(ServerTickEvent.Pre.class, AbstractFabricServerTickEvent.preTickFactory());
        EventManager.post(ServerTickEvent.Post.class, AbstractFabricServerTickEvent.postTickFactory());

        EventManager.post(ServerLevelTickEvent.Pre.class, AbstractFabricServerLevelEvent.preTickFactory());
        EventManager.post(ServerLevelTickEvent.Post.class, AbstractFabricServerLevelEvent.postTickFactory());

        EventManager.post(ServerLevelAddEntityEvent.class, AbstractFabricServerLevelEvent.addEntityFactory());

        EventManager.post(BlockEvent.Break.class, AbstractFabricBlockEvent.breakFactory());
        EventManager.post(BlockEvent.Place.class, AbstractFabricBlockEvent.placeFactory());

        EventManager.post(PlayerEvent.LoggingIn.class, AbstractFabricPlayerEvent.loggingInFactory());
        EventManager.post(PlayerEvent.LoggingOut.class, AbstractFabricPlayerEvent.loggingOutFactory());
        EventManager.post(PlayerEvent.Death.class, AbstractFabricPlayerEvent.deathFactory());
        EventManager.post(PlayerEvent.Clone.class, AbstractFabricPlayerEvent.cloneFactory());
        EventManager.post(PlayerEvent.Attack.class, AbstractFabricPlayerEvent.attackFactory());
        EventManager.post(PlayerEvent.StartTracking.class, AbstractFabricPlayerEvent.startTrackingFactory());

        EventManager.post(RegisterCommandsEvent.class, AbstractFabricRegisterCommandsEvent.registryFactory());
        EventManager.post(RegisterDataPackEvent.class, AbstractFabricRegisterDataPackEvent.registryFactory());
        EventManager.post(RegisterEntityAttributesEvent.class, AbstractFabricRegisterEntityAttributesEvent.registryFactory());
    }
}
