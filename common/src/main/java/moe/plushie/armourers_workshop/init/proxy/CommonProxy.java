package moe.plushie.armourers_workshop.init.proxy;

import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.builder.other.WorldUpdater;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemHandler;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.Scheduler;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.init.ModCommands;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.event.common.BlockEvent;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import moe.plushie.armourers_workshop.init.event.common.EntityEvent;
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
import moe.plushie.armourers_workshop.init.network.UpdateContextPacket;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.platform.ReplayManager;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class CommonProxy {

    public static void init() {
        setup();
        register();
    }

    private static void setup() {
        var library = GlobalSkinLibrary.getInstance();
        SkinLoader.getInstance().register(DataDomain.GLOBAL_SERVER, library::downloadSkin);
        SkinLoader.getInstance().register(DataDomain.GLOBAL_SERVER_PREVIEW, library::downloadPreviewSkin);
        ReplayManager.init();
    }

    private static void register() {

        EventBus.register(RegisterCommandsEvent.class, ModCommands::init);
        EventBus.register(RegisterServerDataPackEvent.class, event -> {
            event.register(DataPackManager.byType(DataPackType.SERVER_DATA));
        });

        EventBus.register(RegisterEntityAttributesEvent.class, event -> {
            event.register(ModEntityTypes.MANNEQUIN.get().get(), MannequinEntity.createLivingAttributes());
            event.register(ModEntityTypes.SEAT.get().get(), SeatEntity.createLivingAttributes());
        });

        EventBus.register(ServerStartingEvent.class, event -> {
            ModLog.debug("hello");
            DataManager.getInstance().connect(EnvironmentManager.getSkinDatabaseDirectory());
            SkinLoader.getInstance().prepare(EnvironmentManager.getDistributionType(event.getServer()));
        });
        EventBus.register(ServerStartedEvent.class, event -> {
            ModLog.debug("init");
            ModContext.init(event.getServer());
            SkinLoader.getInstance().start();
        });

        EventBus.register(ServerStoppingEvent.class, event -> {
            ModLog.debug("wait");
            // before server stopping, we need to sure that all data saved.
            for (var level : event.getServer().getAllLevels()) {
                WorldUpdater.getInstance().drain(level);
            }
            DataManager.getInstance().disconnect();
            SkinLoader.getInstance().stop();
        });
        EventBus.register(ServerStoppedEvent.class, event -> {
            ModLog.debug("bye");
            ModContext.reset();
        });

        EventBus.register(ServerTickEvent.Pre.class, event -> Scheduler.SERVER.begin());
        EventBus.register(ServerTickEvent.Post.class, event -> Scheduler.SERVER.end());

        EventBus.register(DataPackEvent.Sync.class, event -> {
            // when the data pack sync event, we will initialize context.
            if (event.player() instanceof ServerPlayer player) {
                ReplayManager.startRecording(player.server(), player);
                NetworkManager.sendTo(UpdateContextPacket.sync(player), player);
            }
        });

        EventBus.register(PlayerEvent.LoggingIn.class, event -> {
            // when the player login, check and give gifts for holiday
            ModLog.debug("welcome back {}", event.getPlayer().getScoreboardName());
            ModHolidays.welcome(event.getPlayer());
            // when the player login, initialize wardrobe.
            if (event.getPlayer() instanceof ServerPlayer player) {
                NetworkManager.sendWardrobeTo(player, player);
            }
        });
        EventBus.register(PlayerEvent.LoggingOut.class, event -> {
            ModLog.debug("good bye {}", event.getPlayer().getScoreboardName());
            SkinLibraryManager.getServer().remove(event.getPlayer());
            ReplayManager.stopRecording(event.getPlayer().server(), event.getPlayer());
        });
        EventBus.register(PlayerEvent.Death.class, event -> {
            ModLog.debug("keep careful {}", event.getPlayer().getScoreboardName());
            SkinUtils.dropAllIfNeeded((ServerLevel) event.getPlayer().level(), event.getPlayer());
        });
        EventBus.register(PlayerEvent.Clone.class, event -> {
            ModLog.debug("woa {}", event.getPlayer().getScoreboardName());
            SkinUtils.copySkinWardrobe(event.getOriginal(), event.getPlayer());
        });

        EventBus.register(PlayerEvent.Attack.class, event -> {
            var player = event.getPlayer();
            if (player == null || player.isSpectator()) {
                return;
            }
            var itemStack = player.getMainHandItem();
            if (itemStack.getItem() instanceof AbstractItemHandler handler) {
                var result = handler.attackLivingEntity(itemStack, player, event.getTarget());
                if (result.consumesAction()) {
                    event.setCancelled(true);
                }
            }
        });

        EventBus.register(PlayerEvent.StartTracking.class, event -> {
            var entityProfile = ModEntityProfiles.getProfile(event.getTarget());
            if (entityProfile != null) {
                NetworkManager.sendWardrobeTo(event.getTarget(), (ServerPlayer) event.getPlayer());
            }
        });

        EventBus.register(EntityEvent.ReloadSize.class, event -> {
            var collisionShape = event.getEntity().getCustomCollision();
            if (collisionShape != null) {
                event.setSize(event.getSize().withCollisionShape(collisionShape));
            }
        });


        EventBus.register(ServerLevelTickEvent.Pre.class, event -> {
            WorldUpdater.getInstance().tick(event.getLevel());
        });

        EventBus.register(ServerLevelAddEntityEvent.class, event -> {
            SkinUtils.copySkinFromOwner(event.getEntity());
        });

        EventBus.register(BlockEvent.Place.class, BlockUtils::snapshot);
        EventBus.register(BlockEvent.Break.class, BlockUtils::snapshot);
    }
}
