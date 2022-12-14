package moe.plushie.armourers_workshop.init.proxy;

import moe.plushie.armourers_workshop.builder.other.WorldUpdater;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.network.UpdateContextPacket;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.*;
import moe.plushie.armourers_workshop.init.platform.CommonNativeManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class CommonProxy {

    public static void init() {
        register(CommonNativeManager.getProvider());
    }

    private static void register(CommonNativeProvider registries) {

        registries.willRegisterCommand(ModCommands::init);
        registries.willRegisterEntityAttributes(registry -> {
            registry.register(ModEntityTypes.MANNEQUIN.get(), MannequinEntity.createLivingAttributes());
            registry.register(ModEntityTypes.SEAT.get(), SeatEntity.createLivingAttributes());
        });

        registries.willServerTick(WorldUpdater.getInstance()::tick);

        registries.willServerStart(server -> {
            ModLog.debug("hello");
            LocalDataService.start(server);
            SkinLoader.getInstance().setup(server);
        });
        registries.didServerStart(server -> {
            ModLog.debug("init");
            ModContext.init(server);
        });

        registries.willServerStop(server -> {
            ModLog.debug("wait");
            // before server stopping, we need to sure that all data is saved.
            for (ServerLevel level : server.getAllLevels()) {
                WorldUpdater.getInstance().drain(level);
            }
            LocalDataService.stop();
            SkinLoader.getInstance().clear();
        });
        registries.didServerStop(server -> {
            ModLog.debug("bye");
            ModContext.reset();
        });

        registries.willBlockPlace(BlockUtils::snapshot);
        registries.willBlockBreak(BlockUtils::snapshot);

        registries.willPlayerLogin(player -> {
            // when the player login, check and give gifts for holiday
            ModLog.debug("welcome back {}", player.getScoreboardName());
            ModHolidays.welcome(player);
        });
        registries.willPlayerLogout(player -> {
            ModLog.debug("good bye {}", player.getScoreboardName());
            SkinLibraryManager.getServer().remove(player);
        });
        registries.willPlayerDrop(player -> {
            ModLog.debug("keep careful {}", player.getScoreboardName());
            SkinUtils.dropAll(player);
        });
        registries.willPlayerClone((oldPlayer, newPlayer) -> {
            ModLog.debug("woa {}", newPlayer.getScoreboardName());
            SkinWardrobe oldWardrobe = SkinWardrobe.of(oldPlayer);
            SkinWardrobe newWardrobe = SkinWardrobe.of(newPlayer);
            if (newWardrobe != null && oldWardrobe != null) {
                newWardrobe.deserializeNBT(oldWardrobe.serializeNBT());
                newWardrobe.broadcast();
            }
        });

        registries.didPlayerTacking((entity, player) -> {
            EntityProfile entityProfile = ModEntityProfiles.getProfile(entity);
            if (entityProfile != null) {
                NetworkManager.sendWardrobeTo(entity, (ServerPlayer) player);
            }
        });
        registries.didEntityJoin(entity -> {
            SkinUtils.copySkinFromOwner(entity);
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                NetworkManager.sendTo(new UpdateContextPacket(player), player);
                NetworkManager.sendWardrobeTo(player, player);
            }
        });
    }
}
