package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.extend.IExtendedBlockHandler;
import moe.plushie.armourers_workshop.api.extend.IExtendedItemHandler;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.builder.other.WorldUpdater;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.command.FileArgument;
import moe.plushie.armourers_workshop.init.command.ListArgument;
import moe.plushie.armourers_workshop.init.event.ClimbingLocationCheckEvent;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.PreferenceManagerImpl;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.*;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.config.ModConfig;
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
    public void onCommonSetup(FMLLoadCompleteEvent event) {

        ArgumentTypes.register("armourers_workshop:items", ListArgument.class, new ListArgument.Serializer());
        ArgumentTypes.register("armourers_workshop:files", FileArgument.class, new FileArgument.Serializer());

        EntityDataSerializers.registerSerializer(DataSerializers.PLAYER_TEXTURE);

        EnvironmentExecutor.setup(EnvironmentType.COMMON);

        // check
        // IForgeBlock =>
        //                IBlockHandler2
        //                IBlockHandler3
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfig.ModConfigEvent event) {
        PreferenceManagerImpl.reloadSpec(event.getConfig().getSpec());
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
            if (itemStack.getItem() instanceof IExtendedItemHandler) {
                IExtendedItemHandler handler = (IExtendedItemHandler) itemStack.getItem();
                InteractionResult result = handler.attackLivingEntity(itemStack, player, entity);
                if (result.consumesAction()) {
                    event.setCanceled(true);
                }
            }
        }
        @SubscribeEvent
        public void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
            Player player = event.getPlayer();
            if (player.isSpectator()) {
                return;
            }
            Level level = event.getWorld();
            BlockPos blockPos = event.getPos();
            BlockState state = level.getBlockState(blockPos);
            Direction direction = event.getFace();
            IExtendedBlockHandler handler = ObjectUtils.safeCast(state.getBlock(), IExtendedBlockHandler.class);
            if (handler != null) {
                InteractionResult result = handler.attackBlock(level, blockPos, state, direction, player, event.getHand());
                if (result == InteractionResult.CONSUME || result == InteractionResult.FAIL) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public void onItemUseFirst(PlayerInteractEvent.RightClickBlock event) {
            Player player = event.getPlayer();
            if (player.isSpectator()) {
                return;
            }
            InteractionHand hand = event.getHand();
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() instanceof IExtendedItemHandler) {
                IExtendedItemHandler handler = (IExtendedItemHandler) itemStack.getItem();
                UseOnContext context = new UseOnContext(player, hand, event.getHitVec());
                InteractionResult result = handler.useOnFirst(itemStack, context);
                if (result.consumesAction()) {
                    event.setCancellationResult(result);
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public void onAllowClimbing(ClimbingLocationCheckEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (entity.isSpectator()) {
                return;
            }
            BlockPos blockPos = event.getClimbingLocation();
            BlockState blockState = event.getClimbingState();
            IExtendedBlockHandler handler = ObjectUtils.safeCast(blockState.getBlock(), IExtendedBlockHandler.class);
            if (handler != null && handler.isCustomLadder(entity.level, blockPos, blockState, entity)) {
                event.setResult(Event.Result.ALLOW);
            }
        }

        @SubscribeEvent
        public void onAllowBed(SleepingLocationCheckEvent event) {
            BlockPos sleepingPos = event.getSleepingLocation();
            LivingEntity entity = event.getEntityLiving();
            Level level = entity.level;
            BlockState state = level.getBlockState(sleepingPos);
            if (state.getBlock() instanceof IExtendedBlockHandler) {
                IExtendedBlockHandler handler = (IExtendedBlockHandler) state.getBlock();
                if (handler.isCustomBed(entity.level, sleepingPos, state, entity)) {
                    event.setResult(Event.Result.ALLOW);
                }
            }
        }

        @SubscribeEvent
        public void onStopSleep(PlayerWakeUpEvent event) {
            LivingEntity entity = event.getEntityLiving();
            Level level = entity.level;
            BlockPos sleepingPos = entity.blockPosition();
            BlockState state = level.getBlockState(sleepingPos);
            IExtendedBlockHandler handler = ObjectUtils.safeCast(state.getBlock(), IExtendedBlockHandler.class);
            if (handler != null && handler.isCustomBed(level, sleepingPos, state, entity)) {
                level.setBlock(sleepingPos, state.setValue(BedBlock.OCCUPIED, false), 3);
                Vec3 vector3d1 = BedBlock.findStandUpPosition(entity.getType(), level, sleepingPos, entity.yRot).orElseGet(() -> {
                    BlockPos blockpos = sleepingPos.above();
                    return new Vec3((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.1D, (double) blockpos.getZ() + 0.5D);
                });
                Vec3 vector3d2 = Vec3.atBottomCenterOf(sleepingPos).subtract(vector3d1).normalize();
                float f = (float) MathUtils.wrapDegrees(MathUtils.atan2(vector3d2.z, vector3d2.x) * (double) (180F / (float) Math.PI) - 90.0D);
                entity.setPos(vector3d1.x, vector3d1.y, vector3d1.z);
                entity.yRot = f;
                entity.xRot = 0.0F;
            }
        }
    }
}
