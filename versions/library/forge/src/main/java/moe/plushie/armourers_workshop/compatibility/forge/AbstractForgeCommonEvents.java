package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
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

@Available("[1.21, )")
public class AbstractForgeCommonEvents {

    public static final Class<ModConfigEvent> CONFIG = ModConfigEvent.class;
    public static final Class<TickEvent.LevelTickEvent> TICK = TickEvent.LevelTickEvent.class;

    public static final Class<FMLLoadCompleteEvent> FML_LOAD_COMPLETE = FMLLoadCompleteEvent.class;
    public static final Class<FMLClientSetupEvent> FML_CLIENT_SETUP = FMLClientSetupEvent.class;
    public static final Class<FMLCommonSetupEvent> FML_COMMON_SETUP = FMLCommonSetupEvent.class;

    public static final Class<ServerAboutToStartEvent> SERVER_WILL_START = ServerAboutToStartEvent.class;
    public static final Class<ServerStartedEvent> SERVER_DID_START = ServerStartedEvent.class;
    public static final Class<ServerStoppingEvent> SERVER_WILL_STOP = ServerStoppingEvent.class;
    public static final Class<ServerStoppedEvent> SERVER_DID_STOP = ServerStoppedEvent.class;

    public static final Class<BlockEvent.BreakEvent> BLOCK_BREAK = BlockEvent.BreakEvent.class;
    public static final Class<BlockEvent.EntityPlaceEvent> BLOCK_PLACE = BlockEvent.EntityPlaceEvent.class;

    public static final Class<EntityJoinLevelEvent> ENTITY_JOIN = EntityJoinLevelEvent.class;
    public static final Class<LivingDropsEvent> ENTITY_DROPS = LivingDropsEvent.class;
    public static final Class<AttackEntityEvent> ENTITY_ATTACK = AttackEntityEvent.class;

    public static final Class<PlayerEvent.PlayerLoggedInEvent> PLAYER_LOGIN = PlayerEvent.PlayerLoggedInEvent.class;
    public static final Class<PlayerEvent.PlayerLoggedOutEvent> PLAYER_LOGOUT = PlayerEvent.PlayerLoggedOutEvent.class;
    public static final Class<PlayerEvent.Clone> PLAYER_CLONE = PlayerEvent.Clone.class;
    public static final Class<PlayerEvent.StartTracking> PLAYER_TRACKING = PlayerEvent.StartTracking.class;

    public static final Class<RegisterCommandsEvent> COMMAND_REGISTRY = RegisterCommandsEvent.class;
    public static final Class<AddReloadListenerEvent> DATA_PACK_REGISTRY = AddReloadListenerEvent.class;
    public static final Class<EntityAttributeCreationEvent> ENTITY_ATTRIBUTE_REGISTRY = EntityAttributeCreationEvent.class;
}
