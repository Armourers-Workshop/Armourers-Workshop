package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeBlockEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeConfigEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeDataPackEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeEntityEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeLauncherLifecycleEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgePlayerEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeRegisterCommandsEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeRegisterEntityAttributesEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeRegisterServerDataPackEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeServerLevelEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeServerLifecycleEvent;
import moe.plushie.armourers_workshop.compat.forge.event.common.AbstractForgeServerTickEvent;
import moe.plushie.armourers_workshop.init.event.common.BlockEvent;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import moe.plushie.armourers_workshop.init.event.common.EntityEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherClientSetupEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherConfigSetupEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherLoadCompleteEvent;
import moe.plushie.armourers_workshop.init.event.common.PlayerEvent;
import moe.plushie.armourers_workshop.init.event.common.RegisterCommandsEvent;
import moe.plushie.armourers_workshop.init.event.common.RegisterEntityAttributesEvent;
import moe.plushie.armourers_workshop.init.event.common.RegisterServerDataPackEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerLevelAddEntityEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerLevelTickEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStartedEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStoppingEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerTickEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;

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

        EventManager.post(ServerTickEvent.Pre.class, AbstractForgeServerTickEvent.preTickFactory());
        EventManager.post(ServerTickEvent.Post.class, AbstractForgeServerTickEvent.postTickFactory());

        EventManager.post(ServerLevelTickEvent.Pre.class, AbstractForgeServerLevelEvent.preTickFactory());
        EventManager.post(ServerLevelTickEvent.Post.class, AbstractForgeServerLevelEvent.postTickFactory());

        EventManager.post(ServerLevelAddEntityEvent.class, AbstractForgeServerLevelEvent.addEntityFactory());

        EventManager.post(BlockEvent.Destroy.class, AbstractForgeBlockEvent.destroyFactory());
        EventManager.post(BlockEvent.Place.class, AbstractForgeBlockEvent.placeFactory());

        EventManager.post(PlayerEvent.LoggingIn.class, AbstractForgePlayerEvent.loggingInFactory());
        EventManager.post(PlayerEvent.LoggingOut.class, AbstractForgePlayerEvent.loggingOutFactory());
        EventManager.post(PlayerEvent.Death.class, AbstractForgePlayerEvent.deathFactory());
        EventManager.post(PlayerEvent.Clone.class, AbstractForgePlayerEvent.cloneFactory());
        EventManager.post(PlayerEvent.Attack.class, AbstractForgePlayerEvent.attackFactory());
        EventManager.post(PlayerEvent.StartTracking.class, AbstractForgePlayerEvent.startTrackingFactory());

        EventManager.post(EntityEvent.ReloadSize.class, AbstractForgeEntityEvent.reloadSizeFactory());

        EventManager.post(DataPackEvent.Sync.class, AbstractForgeDataPackEvent.syncFactory());

        EventManager.post(RegisterCommandsEvent.class, AbstractForgeRegisterCommandsEvent.registryFactory());
        EventManager.post(RegisterServerDataPackEvent.class, AbstractForgeRegisterServerDataPackEvent.registryFactory());
        EventManager.post(RegisterEntityAttributesEvent.class, AbstractForgeRegisterEntityAttributesEvent.registryFactory());
    }
}
