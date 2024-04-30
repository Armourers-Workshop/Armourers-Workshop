package moe.plushie.armourers_workshop.init.platform.fabric.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IBlockHandler;
import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.EnvironmentManagerImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CommonProxyImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        ArmourersWorkshop.init();

        // prioritize handle.
        EventManager.listen(ServerStartingEvent.class, event -> EnvironmentManagerImpl.attach(event.getServer()));
        EventManager.listen(ServerStoppedEvent.class, event -> EnvironmentManagerImpl.detach(event.getServer()));

        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
        EnvironmentExecutor.willSetup(EnvironmentType.COMMON);

        UseBlockCallback.EVENT.register(this::onUseItemFirst);
        EntitySleepEvents.ALLOW_BED.register(this::onAllowBed);
        EntitySleepEvents.STOP_SLEEPING.register(this::onStopSleep);
        EntityLifecycleEvents.ALLOW_CLIMBING.register(this::onAllowClimbing);

        AttackBlockCallback.EVENT.register(this::onBlockBreakPre);

        EnvironmentExecutor.didInit(EnvironmentType.COMMON);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.COMMON, FabricLoader.getInstance().getConfigDir());
//        CommonEventRegistries.getInstance().didServerStop(server -> {
//            FabricConfigTracker.INSTANCE.unloadConfigs(FabricConfig.Type.SERVER, FabricLoader.getInstance().getConfigDir());
//        });

        EnvironmentExecutor.didSetup(EnvironmentType.COMMON);
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
        if (handler != null && handler.isCustomLadder(entity.getLevel(), blockPos, blockState, entity)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult onAllowBed(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult) {
        IBlockHandler handler = ObjectUtils.safeCast(state.getBlock(), IBlockHandler.class);
        if (handler != null && handler.isCustomBed(entity.getLevel(), sleepingPos, state, entity)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void onStopSleep(LivingEntity entity, BlockPos sleepingPos) {
        Level level = entity.getLevel();
        BlockState state = level.getBlockState(sleepingPos);
        IBlockHandler handler = ObjectUtils.safeCast(state.getBlock(), IBlockHandler.class);
        if (handler != null && handler.isCustomBed(level, sleepingPos, state, entity)) {
            entity.stopSleeping(sleepingPos);
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
