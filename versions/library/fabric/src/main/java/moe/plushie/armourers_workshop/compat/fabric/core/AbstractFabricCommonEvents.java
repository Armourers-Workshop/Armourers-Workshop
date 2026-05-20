package moe.plushie.armourers_workshop.compat.fabric.core;

import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricBlockEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricConfigEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricEntityEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricDataPackEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricLauncherLifecycleEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricPlayerEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricRegisterCommandsEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricRegisterEntityAttributesEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricRegisterServerDataPackEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricServerLevelEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricServerLifecycleEvent;
import moe.plushie.armourers_workshop.compat.fabric.core.event.AbstractFabricServerTickEvent;
import moe.plushie.armourers_workshop.init.event.common.BlockEvent;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
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
import moe.plushie.armourers_workshop.init.platform.fabric.event.common.FabricEntityEvent;

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

        EventManager.post(BlockEvent.Destroy.class, AbstractFabricBlockEvent.destroyFactory());

        EventManager.post(PlayerEvent.LoggingIn.class, AbstractFabricPlayerEvent.loggingInFactory());
        EventManager.post(PlayerEvent.LoggingOut.class, AbstractFabricPlayerEvent.loggingOutFactory());
        EventManager.post(PlayerEvent.Death.class, AbstractFabricPlayerEvent.deathFactory());
        EventManager.post(PlayerEvent.Clone.class, AbstractFabricPlayerEvent.cloneFactory());
        EventManager.post(PlayerEvent.Attack.class, AbstractFabricPlayerEvent.attackFactory());

        EventManager.post(DataPackEvent.Sync.class, AbstractFabricDataPackEvent.syncFactory());

        EventManager.post(RegisterCommandsEvent.class, AbstractFabricRegisterCommandsEvent.registryFactory());
        EventManager.post(RegisterServerDataPackEvent.class, AbstractFabricRegisterServerDataPackEvent.registryFactory());
        EventManager.post(RegisterEntityAttributesEvent.class, AbstractFabricRegisterEntityAttributesEvent.registryFactory());

        EventManager.post(FabricEntityEvent.StartSleep.class, AbstractFabricEntityEvent.startSleepFactory());
        EventManager.post(FabricEntityEvent.StopSleep.class, AbstractFabricEntityEvent.stopSleepFactory());

        EventManager.post(FabricEntityEvent.UseBlock.class, AbstractFabricEntityEvent.useBlockFactory());
        EventManager.post(FabricEntityEvent.AttackBlock.class, AbstractFabricEntityEvent.attackBlockFactory());
    }
}
