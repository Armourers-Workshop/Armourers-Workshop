package moe.plushie.armourers_workshop.init.proxy;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.builder.other.WorldUpdater;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinServerType;
import moe.plushie.armourers_workshop.init.ModCommands;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.network.UpdateContextPacket;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.platform.ReplayManager;
import moe.plushie.armourers_workshop.init.platform.event.common.BlockEvent;
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
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CommonProxy {

    public static void init() {
        setup();
        register();
    }

    private static void setup() {
        GlobalSkinLibrary library = GlobalSkinLibrary.getInstance();
        SkinLoader.getInstance().register(DataDomain.GLOBAL_SERVER, library::downloadSkin);
        SkinLoader.getInstance().register(DataDomain.GLOBAL_SERVER_PREVIEW, library::downloadPreviewSkin);
        ReplayManager.init();
    }

    private static void register() {

        EventManager.listen(RegisterCommandsEvent.class, ModCommands::init);
        EventManager.listen(RegisterDataPackEvent.class, event -> {
            event.register(DataPackManager.byType(DataPackType.SERVER_DATA));
        });

        EventManager.listen(RegisterEntityAttributesEvent.class, event -> {
            event.register(ModEntityTypes.MANNEQUIN.get().get(), MannequinEntity.createLivingAttributes());
            event.register(ModEntityTypes.SEAT.get().get(), SeatEntity.createLivingAttributes());
        });

        EventManager.listen(ServerStartingEvent.class, event -> {
            ModLog.debug("hello");
            LocalDataService.start(EnvironmentManager.getSkinDatabaseDirectory());
            SkinLoader.getInstance().prepare(SkinServerType.of(event.getServer()));
        });
        EventManager.listen(ServerStartedEvent.class, event -> {
            ModLog.debug("init");
            ModContext.init(event.getServer());
            SkinLoader.getInstance().start();
        });

        EventManager.listen(ServerStoppingEvent.class, event -> {
            ModLog.debug("wait");
            // before server stopping, we need to sure that all data saved.
            for (ServerLevel level : event.getServer().getAllLevels()) {
                WorldUpdater.getInstance().drain(level);
            }
            LocalDataService.stop();
            SkinLoader.getInstance().stop();
        });
        EventManager.listen(ServerStoppedEvent.class, event -> {
            ModLog.debug("bye");
            ModContext.reset();
        });

        EventManager.listen(PlayerEvent.LoggingIn.class, event -> {
            // when the player login, check and give gifts for holiday
            ModLog.debug("welcome back {}", event.getPlayer().getScoreboardName());
            ModHolidays.welcome(event.getPlayer());
            ReplayManager.startRecording(event.getPlayer().getServer(), event.getPlayer());
            // When the player login, initialize context and wardrobe.
            if (event.getPlayer() instanceof ServerPlayer player) {
                NetworkManager.sendTo(new UpdateContextPacket(player), player);
                NetworkManager.sendWardrobeTo(player, player);
            }
        });
        EventManager.listen(PlayerEvent.LoggingOut.class, event -> {
            ModLog.debug("good bye {}", event.getPlayer().getScoreboardName());
            SkinLibraryManager.getServer().remove(event.getPlayer());
            ReplayManager.stopRecording(event.getPlayer().getServer(), event.getPlayer());
        });
        EventManager.listen(PlayerEvent.Death.class, event -> {
            ModLog.debug("keep careful {}", event.getPlayer().getScoreboardName());
            SkinUtils.dropAll(event.getPlayer());
        });
        EventManager.listen(PlayerEvent.Clone.class, event -> {
            ModLog.debug("woa {}", event.getPlayer().getScoreboardName());
            SkinWardrobe oldWardrobe = SkinWardrobe.of(event.getOriginal());
            SkinWardrobe newWardrobe = SkinWardrobe.of(event.getPlayer());
            if (newWardrobe != null && oldWardrobe != null) {
                CompoundTag tag = new CompoundTag();
                oldWardrobe.serialize(AbstractDataSerializer.wrap(tag, event.getPlayer()));
                newWardrobe.deserialize(AbstractDataSerializer.wrap(tag, event.getPlayer()));
                newWardrobe.broadcast();
            }
        });

        EventManager.listen(PlayerEvent.Attack.class, event -> {
            Player player = event.getPlayer();
            if (player == null || player.isSpectator()) {
                return;
            }
            ItemStack itemStack = player.getMainHandItem();
            IItemHandler handler = ObjectUtils.safeCast(itemStack.getItem(), IItemHandler.class);
            if (handler != null) {
                InteractionResult result = handler.attackLivingEntity(itemStack, player, event.getTarget());
                if (result.consumesAction()) {
                    event.setCancelled(true);
                }
            }
        });

        EventManager.listen(PlayerEvent.StartTracking.class, event -> {
            EntityProfile entityProfile = ModEntityProfiles.getProfile(event.getTarget());
            if (entityProfile != null) {
                NetworkManager.sendWardrobeTo(event.getTarget(), (ServerPlayer) event.getPlayer());
            }
        });

        EventManager.listen(ServerLevelTickEvent.class, event -> {
            WorldUpdater.getInstance().tick(event.getLevel());
        });
        EventManager.listen(ServerLevelAddEntityEvent.class, event -> {
            SkinUtils.copySkinFromOwner(event.getEntity());
        });

        EventManager.listen(BlockEvent.Place.class, BlockUtils::snapshot);
        EventManager.listen(BlockEvent.Break.class, BlockUtils::snapshot);
    }
}
