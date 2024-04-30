package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import manifold.ext.rt.api.auto;

@Available("[1.21, )")
public class AbstractForgeCommonEventsImpl {

    public static final auto FML_CONFIG = AbstractForgeEventBus.create(ModConfigEvent.class);

    public static final auto FML_LOAD_COMPLETE = AbstractForgeEventBus.create(FMLLoadCompleteEvent.class);
    public static final auto FML_CLIENT_SETUP = AbstractForgeEventBus.create(FMLClientSetupEvent.class);
    public static final auto FML_COMMON_SETUP = AbstractForgeEventBus.create(FMLCommonSetupEvent.class);

    public static final auto SERVER_WILL_START = AbstractForgeEventBus.create(ServerAboutToStartEvent.class);
    public static final auto SERVER_DID_START = AbstractForgeEventBus.create(ServerStartedEvent.class);
    public static final auto SERVER_WILL_STOP = AbstractForgeEventBus.create(ServerStoppingEvent.class);
    public static final auto SERVER_DID_STOP = AbstractForgeEventBus.create(ServerStoppedEvent.class);

    public static final auto SERVER_LEVEL_TICK_PRE = AbstractForgeEventBus.create(LevelTickEvent.Pre.class);

    public static final auto BLOCK_BREAK = AbstractForgeEventBus.create(BlockEvent.BreakEvent.class);
    public static final auto BLOCK_PLACE = AbstractForgeEventBus.create(BlockEvent.EntityPlaceEvent.class);

    public static final auto SERVER_LEVEL_ADD_ENTITY = AbstractForgeEventBus.create(EntityJoinLevelEvent.class);
    public static final auto PLAYER_DEATH = AbstractForgeEventBus.create(LivingDropsEvent.class);
    public static final auto PLAYER_ATTACK = AbstractForgeEventBus.create(AttackEntityEvent.class);

    public static final auto PLAYER_LOGIN = AbstractForgeEventBus.create(PlayerEvent.PlayerLoggedInEvent.class);
    public static final auto PLAYER_LOGOUT = AbstractForgeEventBus.create(PlayerEvent.PlayerLoggedOutEvent.class);
    public static final auto PLAYER_CLONE = AbstractForgeEventBus.create(PlayerEvent.Clone.class);
    public static final auto PLAYER_TRACKING = AbstractForgeEventBus.create(PlayerEvent.StartTracking.class);

    public static final auto COMMAND_REGISTRY = AbstractForgeEventBus.create(RegisterCommandsEvent.class);
    public static final auto DATA_PACK_REGISTRY = AbstractForgeEventBus.create(AddReloadListenerEvent.class);
    public static final auto ENTITY_ATTRIBUTE_REGISTRY = AbstractForgeEventBus.create(EntityAttributeCreationEvent.class);
}
