package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.builder.other.WorldUpdater;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventDispatcher;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.*;
import moe.plushie.armourers_workshop.init.command.ColorArgument;
import moe.plushie.armourers_workshop.init.command.ColorSchemeArgument;
import moe.plushie.armourers_workshop.init.command.FileArgument;
import moe.plushie.armourers_workshop.init.command.ListArgument;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ConfigBuilderImpl;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonEventDispatcherImpl extends AbstractForgeCommonEventDispatcher {

    public static void init() {
        CommonEventDispatcherImpl dispatcher = new CommonEventDispatcherImpl();
        FMLJavaModLoadingContext.get().getModEventBus().register(dispatcher);
        MinecraftForge.EVENT_BUS.register(new Forge());
        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
    }

    @SubscribeEvent
    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANNEQUIN.get(), MannequinEntity.createLivingAttributes().build());
        event.put(ModEntities.SEAT.get(), SeatEntity.createLivingAttributes().build());
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        ArgumentTypes.register(ModConstants.key("items").toString(), ListArgument.class, new ListArgument.Serializer());
        ArgumentTypes.register(ModConstants.key("files").toString(), FileArgument.class, new FileArgument.Serializer());
        ArgumentTypes.register(ModConstants.key("dye").toString(), ColorSchemeArgument.class, new ColorSchemeArgument.Serializer());
        ArgumentTypes.register(ModConstants.key("color").toString(), ColorArgument.class, new ColorArgument.Serializer());

        EntityDataSerializers.registerSerializer(DataSerializers.PLAYER_TEXTURE);

        EnvironmentExecutor.didInit(EnvironmentType.COMMON);
    }

    @SubscribeEvent
    public void onCommonFinish(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.COMMON));
    }

    @Override
    public void configDidReload(ForgeConfigSpec spec) {
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.CLIENT, spec);
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.COMMON, spec);
    }

    private static class Forge extends Handler {

        @Override
        public void serverWillStart(MinecraftServer server) {
            ModLog.debug("hello");
            LocalDataService.start(server);
            SkinLoader.getInstance().setup(server);
        }

        @Override
        public void serverDidStart(MinecraftServer server) {
            ModContext.init(server);
        }

        @Override
        public void serverWillStop(MinecraftServer server) {
            // before server stopping, we need to sure that all data is saved.
            for (ServerLevel level : server.getAllLevels()) {
                WorldUpdater.getInstance().drain(level);
            }
            LocalDataService.stop();
            SkinLoader.getInstance().clear();
        }

        @Override
        public void serverDidStop(MinecraftServer server) {
            ModLog.debug("bye");
            ModContext.reset();
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.WorldTickEvent event) {
            if (event.side == LogicalSide.SERVER) {
                WorldUpdater.getInstance().tick(event.world);
            }
        }

        @SubscribeEvent
        public void registerCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(ModCommands.commands());
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            // when the player login, check and give gifts for holiday
            ModHolidays.welcome(event.getPlayer());
        }

        @SubscribeEvent
        public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
            SkinLibraryManager.getServer().remove(event.getPlayer());
        }

        @SubscribeEvent
        public void onPlayerClone(PlayerEvent.Clone event) {
            SkinWardrobe oldWardrobe = SkinWardrobe.of(event.getOriginal());
            SkinWardrobe newWardrobe = SkinWardrobe.of(event.getPlayer());
            if (newWardrobe != null && oldWardrobe != null) {
                newWardrobe.deserializeNBT(oldWardrobe.serializeNBT());
                newWardrobe.broadcast();
            }
        }

        @SubscribeEvent
        public void onPlayerDrops(LivingDropsEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (entity instanceof Player) {
                SkinUtils.dropAll((Player) entity);
            }
        }

        @SubscribeEvent
        public void onStartTracking(PlayerEvent.StartTracking event) {
            Entity entity = event.getTarget();
            EntityProfile entityProfile = ModEntityProfiles.getProfile(entity);
            if (entityProfile != null) {
                NetworkManager.sendWardrobeTo(entity, (ServerPlayer) event.getPlayer());
            }
        }

        @SubscribeEvent
        public void onEntityJoinWorld(EntityJoinWorldEvent event) {
            if (event.getWorld().isClientSide()) {
                return;
            }
            Entity entity = event.getEntity();
            SkinUtils.copySkinFromOwner(entity);
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                NetworkManager.sendContextTo(player);
                NetworkManager.sendWardrobeTo(player, player);
            }
        }

        @SubscribeEvent
        public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            Block block = event.getState().getBlock();
            if (event.getEntity() instanceof ServerPlayer && block instanceof SkinCubeBlock) {
                Player player = (Player) event.getEntity();
                LevelAccessor level = event.getWorld();
                BlockSnapshot snapshot = event.getBlockSnapshot();
                Component reason = TranslateUtils.title("chat.armourers_workshop.undo.placeBlock");
                //#if MC >= 11800
                CompoundTag tag = snapshot.getTag();
                //#else
                //# CompoundTag tag = snapshot.getNbt();
                //#endif
                BlockUtils.snapshot(level, event.getPos(), snapshot.getReplacedBlock(), tag, player, reason);
            }
        }

        @SubscribeEvent
        public void onBlockBreak(BlockEvent.BreakEvent event) {
            Block block = event.getState().getBlock();
            if (event.getPlayer() instanceof ServerPlayer && block instanceof SkinCubeBlock) {
                LevelAccessor level = event.getWorld();
                BlockEntity blockEntity = level.getBlockEntity(event.getPos());
                CompoundTag oldNBT = null;
                if (blockEntity != null) {
                    oldNBT = DataSerializers.saveBlockTag(blockEntity);
                }
                Component reason = TranslateUtils.title("chat.armourers_workshop.undo.breakBlock");
                BlockUtils.snapshot(level, event.getPos(), event.getState(), oldNBT, event.getPlayer(), reason);
            }
        }

        @SubscribeEvent
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getPlayer();
            if (player.isSpectator()) {
                return;
            }
            Entity entity = event.getTarget();
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.getItem() instanceof IItemHandler) {
                IItemHandler handler = (IItemHandler) itemStack.getItem();
                InteractionResult result = handler.attackLivingEntity(itemStack, player, entity);
                if (result.consumesAction()) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
