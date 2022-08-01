package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.builder.other.WorldUpdater;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonEventDispatcher {

    public static void init() {
        CommonEventDispatcher dispatcher = new CommonEventDispatcher();
        FMLJavaModLoadingContext.get().getModEventBus().register(dispatcher);
        MinecraftForge.EVENT_BUS.register(new Forge());
    }

    @SubscribeEvent
    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANNEQUIN.get(), MannequinEntity.createLivingAttributes().build());
        event.put(ModEntities.SEAT.get(), SeatEntity.createLivingAttributes().build());
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        ArgumentTypes.register("armourers_workshop:items", ListArgument.class, new ListArgument.Serializer());
        ArgumentTypes.register("armourers_workshop:files", FileArgument.class, new FileArgument.Serializer());

        EntityDataSerializers.registerSerializer(DataSerializers.PLAYER_TEXTURE);

        EnvironmentExecutor.setup(EnvironmentType.COMMON);
    }

    @SubscribeEvent
    public void onCommonFinish(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> EnvironmentExecutor.finish(EnvironmentType.COMMON));
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfig.ModConfigEvent event) {
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.CLIENT, event.getConfig().getSpec());
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.COMMON, event.getConfig().getSpec());
    }

    private static class Forge {

        @SubscribeEvent
        public void onServerTick(TickEvent.WorldTickEvent event) {
            if (event.side == LogicalSide.SERVER) {
                WorldUpdater.getInstance().tick(event.world);
            }
        }

        @SubscribeEvent
        public void onServerStart(FMLServerAboutToStartEvent event) {
            ModLog.debug("hello");
            LocalDataService.start(event.getServer());
            SkinLoader.getInstance().setup(event.getServer());
        }

        @SubscribeEvent
        public void onServerDidStart(FMLServerStartedEvent event) {
            ModContext.init(event.getServer());
        }

        @SubscribeEvent
        public void onServerWillStop(FMLServerStoppingEvent event) {
            LocalDataService.stop();
            SkinLoader.getInstance().clear();
        }

        @SubscribeEvent
        public void onServerStop(FMLServerStoppedEvent event) {
            ModLog.debug("bye");
            ModContext.reset();
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
                BlockUtils.snapshot(level, event.getPos(), snapshot.getReplacedBlock(), snapshot.getNbt(), player, reason);
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
                    oldNBT = blockEntity.save(new CompoundTag());
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
