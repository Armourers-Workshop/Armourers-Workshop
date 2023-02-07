package moe.plushie.armourers_workshop.init.platform.fabric.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IBlockHandler;
import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.fabric.CommonNativeManagerImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.EnvironmentManagerImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ConfigBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigEvents;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityClimbingEvents;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CommonProxyImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        ArmourersWorkshop.init();
        EnvironmentExecutor.willInit(EnvironmentType.COMMON);

        CommonNativeManagerImpl.INSTANCE.didServerStart(EnvironmentManagerImpl::attach);
        CommonNativeManagerImpl.INSTANCE.didServerStop(EnvironmentManagerImpl::detach);

        AttackEntityCallback.EVENT.register(this::onAttackEntity);
        UseBlockCallback.EVENT.register(this::onUseItemFirst);
        EntitySleepEvents.ALLOW_BED.register(this::onAllowBed);
        EntitySleepEvents.STOP_SLEEPING.register(this::onStopSleep);
        EntityClimbingEvents.ALLOW_CLIMBING.register(this::onAllowClimbing);

        AttackBlockCallback.EVENT.register(this::onBlockBreakPre);

        FabricConfigEvents.LOADING.register(this::onConfigReloaded);
        FabricConfigEvents.RELOADING.register(this::onConfigReloaded);

        EnvironmentExecutor.didInit(EnvironmentType.COMMON);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.COMMON, FabricLoader.getInstance().getConfigDir());
//        CommonEventRegistries.getInstance().didServerStop(server -> {
//            FabricConfigTracker.INSTANCE.unloadConfigs(FabricConfig.Type.SERVER, FabricLoader.getInstance().getConfigDir());
//        });

        EnvironmentExecutor.didSetup(EnvironmentType.COMMON);
    }

    public void onConfigReloaded(FabricConfig config) {
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.CLIENT, config.getSpec());
        ConfigBuilderImpl.reloadSpec(ModConfigSpec.COMMON, config.getSpec());
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
            float yRot = entity.getYRot();
            Vec3 vector3d1 = BedBlock.findStandUpPosition(entity.getType(), level, sleepingPos, Direction.UP, yRot).orElseGet(() -> {
                BlockPos blockpos = sleepingPos.above();
                return new Vec3((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.1D, (double) blockpos.getZ() + 0.5D);
            });
            Vec3 vector3d2 = Vec3.atBottomCenterOf(sleepingPos).subtract(vector3d1).normalize();
            float f = (float) MathUtils.wrapDegrees(MathUtils.atan2(vector3d2.z, vector3d2.x) * (double) (180F / (float) Math.PI) - 90.0D);
            entity.setPos(vector3d1.x, vector3d1.y, vector3d1.z);
            entity.setYRot(f);
            entity.setXRot(0);
        }
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
