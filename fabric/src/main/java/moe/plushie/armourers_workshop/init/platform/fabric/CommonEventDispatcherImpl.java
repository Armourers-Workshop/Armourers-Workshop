package moe.plushie.armourers_workshop.init.platform.fabric;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IBlockHandler;
import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.builder.other.WorldUpdater;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.*;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigEvents;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ConfigBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityClimbingEvents;
import moe.plushie.armourers_workshop.init.platform.fabric.event.PlayerBlockPlaceEvents;
import moe.plushie.armourers_workshop.init.command.ColorArgument;
import moe.plushie.armourers_workshop.init.command.ColorSchemeArgument;
import moe.plushie.armourers_workshop.init.command.FileArgument;
import moe.plushie.armourers_workshop.init.command.ListArgument;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CommonEventDispatcherImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        ArmourersWorkshop.init();
        EnvironmentExecutor.willInit(EnvironmentType.COMMON);

        CommandRegistrationCallback.EVENT.register(this::registerCommands);

        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerDidStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerWillStop);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStop);
        ServerTickEvents.START_WORLD_TICK.register(this::onServerTick);

        ServerPlayerEvents.COPY_FROM.register(this::onPlayerClone);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerLogin);
        ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerLogout);

        ServerEntityEvents.ENTITY_LOAD.register(this::onEntityJoinWorld);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(this::onPlayerDrops);

        EntityTrackingEvents.START_TRACKING.register(this::onStartTracking);

        AttackEntityCallback.EVENT.register(this::onAttackEntity);
        UseBlockCallback.EVENT.register(this::onUseItemFirst);
        EntitySleepEvents.ALLOW_BED.register(this::onAllowBed);
        EntitySleepEvents.STOP_SLEEPING.register(this::onStopSleep);
        EntityClimbingEvents.ALLOW_CLIMBING.register(this::onAllowClimbing);

        AttackBlockCallback.EVENT.register(this::onBlockBreakPre);

        PlayerBlockBreakEvents.BEFORE.register(this::onBlockBreak);
        PlayerBlockPlaceEvents.BEFORE.register(this::onBlockPlace);

        FabricConfigEvents.LOADING.register(this::onConfigReloaded);
        FabricConfigEvents.RELOADING.register(this::onConfigReloaded);

        registerEntityAttributes();

        onCommonSetup();
        EnvironmentExecutor.didInit(EnvironmentType.COMMON);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.COMMON, FabricLoader.getInstance().getConfigDir());

        EnvironmentExecutor.didSetup(EnvironmentType.COMMON);
    }

    public void registerEntityAttributes() {
        // noinspection all
        FabricDefaultAttributeRegistry.register(ModEntities.MANNEQUIN.get(), MannequinEntity.createLivingAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.SEAT.get(), SeatEntity.createLivingAttributes());
    }

    public void onCommonSetup() {
        ArgumentTypes.register("armourers_workshop:items", ListArgument.class, new ListArgument.Serializer());
        ArgumentTypes.register("armourers_workshop:files", FileArgument.class, new FileArgument.Serializer());
        ArgumentTypes.register("armourers_workshop:dye", ColorSchemeArgument.class, new ColorSchemeArgument.Serializer());
        ArgumentTypes.register("armourers_workshop:color", ColorArgument.class, new ColorArgument.Serializer());

        EntityDataSerializers.registerSerializer(DataSerializers.PLAYER_TEXTURE);
    }

    public void onConfigReloaded(FabricConfig config) {
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.CLIENT, config.getSpec());
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.COMMON, config.getSpec());
    }

    public void onServerTick(ServerLevel level) {
        WorldUpdater.getInstance().tick(level);
    }

    public void onServerStart(MinecraftServer server) {
        ModLog.debug("hello");
        NetworkManagerImpl.CURRENT_SERVER = server;
        LocalDataService.start(server);
        SkinLoader.getInstance().setup(server);
    }

    public void onServerDidStart(MinecraftServer server) {
        ModContext.init(server);
    }

    public void onServerWillStop(MinecraftServer server) {
        // before server stopping, we need to sure that all data is saved.
        for (ServerLevel level : server.getAllLevels()) {
            WorldUpdater.getInstance().drain(level);
        }
        LocalDataService.stop();
        SkinLoader.getInstance().clear();
    }

    public void onServerStop(MinecraftServer server) {
        ModLog.debug("bye");
        ModContext.reset();
        NetworkManagerImpl.CURRENT_SERVER = null;
        FabricConfigTracker.INSTANCE.unloadConfigs(FabricConfig.Type.SERVER, FabricLoader.getInstance().getConfigDir());
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        dispatcher.register(ModCommands.commands());
    }

    public void onPlayerLogin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        // when the player login, check and give gifts for holiday
        ModHolidays.welcome(handler.player);
    }

    public void onPlayerLogout(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        SkinLibraryManager.getServer().remove(handler.player);
    }

    public void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        SkinWardrobe oldWardrobe = SkinWardrobe.of(oldPlayer);
        SkinWardrobe newWardrobe = SkinWardrobe.of(newPlayer);
        if (newWardrobe != null && oldWardrobe != null) {
            newWardrobe.deserializeNBT(oldWardrobe.serializeNBT());
            newWardrobe.broadcast();
        }
    }

    public void onPlayerDrops(ServerLevel level, Entity entity, LivingEntity killedEntity) {
        if (killedEntity instanceof Player) {
            SkinUtils.dropAll((Player) killedEntity);
        }
    }

    public void onStartTracking(Entity entity, ServerPlayer player) {
        EntityProfile entityProfile = ModEntityProfiles.getProfile(entity);
        if (entityProfile != null) {
            NetworkManager.sendWardrobeTo(entity, player);
        }
    }

    public void onEntityJoinWorld(Entity entity, ServerLevel level) {
        SkinUtils.copySkinFromOwner(entity);
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            NetworkManager.sendContextTo(player);
            NetworkManager.sendWardrobeTo(player, player);
        }
    }

    public InteractionResult onAttackEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }
        ItemStack itemStack = player.getItemInHand(hand);
        IItemHandler handler = ObjectUtils.safeCast(itemStack.getItem(), IItemHandler.class);
        if (handler != null) {
            InteractionResult result = handler.attackLivingEntity(itemStack, player, entity);
            if (result.consumesAction()) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult onUseItemFirst(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }
        ItemStack itemStack = player.getItemInHand(hand);
        IItemHandler handler = ObjectUtils.safeCast(itemStack.getItem(), IItemHandler.class);
        if (handler != null) {
            return handler.useOnFirst(itemStack, new UseOnContext(player, hand, hitResult));
        }
        return InteractionResult.PASS;
    }

    public InteractionResult onAllowClimbing(LivingEntity entity, BlockPos blockPos, BlockState blockState) {
        if (entity.isSpectator()) {
            return InteractionResult.PASS;
        }
        IBlockHandler handler = ObjectUtils.safeCast(blockState.getBlock(), IBlockHandler.class);
        if (handler != null && handler.isCustomLadder(entity.level, blockPos, blockState, entity)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult onAllowBed(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult) {
        IBlockHandler handler = ObjectUtils.safeCast(state.getBlock(), IBlockHandler.class);
        if (handler != null && handler.isCustomBed(entity.level, sleepingPos, state, entity)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void onStopSleep(LivingEntity entity, BlockPos sleepingPos) {
        Level level = entity.level;
        BlockState state = level.getBlockState(sleepingPos);
        IBlockHandler handler = ObjectUtils.safeCast(state.getBlock(), IBlockHandler.class);
        if (handler != null && handler.isCustomBed(level, sleepingPos, state, entity)) {
            level.setBlock(sleepingPos, state.setValue(BedBlock.OCCUPIED, false), 3);
            //#if MC >= 11800
            float yRot = entity.getYRot();
            //#else
            //# float yRot = entity.yRot;
            //#endif
            Vec3 vector3d1 = BedBlock.findStandUpPosition(entity.getType(), level, sleepingPos, yRot).orElseGet(() -> {
                BlockPos blockpos = sleepingPos.above();
                return new Vec3((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.1D, (double) blockpos.getZ() + 0.5D);
            });
            Vec3 vector3d2 = Vec3.atBottomCenterOf(sleepingPos).subtract(vector3d1).normalize();
            float f = (float) MathUtils.wrapDegrees(MathUtils.atan2(vector3d2.z, vector3d2.x) * (double) (180F / (float) Math.PI) - 90.0D);
            entity.setPos(vector3d1.x, vector3d1.y, vector3d1.z);
            //#if MC >= 11800
            entity.setYRot(f);
            entity.setXRot(0);
            //#else
            //# entity.yRot = f;
            //# entity.xRot = 0;
            //#endif
        }
    }

    public boolean onBlockPlace(BlockPlaceContext blockPlaceContext, BlockState blockState) {
        Block block = blockState.getBlock();
        Player player = blockPlaceContext.getPlayer();
        if (player instanceof ServerPlayer && block instanceof SkinCubeBlock) {
            Level level = blockPlaceContext.getLevel();
            BlockPos blockPos = blockPlaceContext.getClickedPos();
            BlockState oldState = level.getBlockState(blockPos);
            CompoundTag oldNBT = DataSerializers.saveBlockTag(level.getBlockEntity(blockPos));
            Component reason = TranslateUtils.title("chat.armourers_workshop.undo.placeBlock");
            BlockUtils.snapshot(level, blockPos, oldState, oldNBT, player, reason);
        }
        return true;
    }

    public boolean onBlockBreak(Level level, Player player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) {
        Block block = state.getBlock();
        if (player instanceof ServerPlayer && block instanceof SkinCubeBlock) {
            CompoundTag oldNBT = DataSerializers.saveBlockTag(level.getBlockEntity(pos));
            Component reason = TranslateUtils.title("chat.armourers_workshop.undo.breakBlock");
            BlockUtils.snapshot(level, pos, state, oldNBT, player, reason);
        }
        return true;
    }

    public InteractionResult onBlockBreakPre(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }
        BlockState state = level.getBlockState(pos);
        IBlockHandler handler = ObjectUtils.safeCast(state.getBlock(), IBlockHandler.class);
        if (handler != null) {
            InteractionResult result = handler.attackBlock(level, pos, state, direction, player, hand);
            if (result == InteractionResult.CONSUME) {
                return InteractionResult.FAIL;
            }
            if (result == InteractionResult.SUCCESS) {
                return InteractionResult.PASS;
            }
            return result;
        }
        return InteractionResult.PASS;
    }

}
