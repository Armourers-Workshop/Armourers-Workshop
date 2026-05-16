package moe.plushie.armourers_workshop.init.platform.fabric.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionHand;
import moe.plushie.armourers_workshop.compat.core.block.AbstractBlock;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemHandler;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.PlatformManagerImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CommonProxyImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        ArmourersWorkshop.init();

        // prioritize handle.
        EventBus.register(ServerStartingEvent.class, event -> PlatformManagerImpl.attach(event.server()));
        EventBus.register(ServerStoppedEvent.class, event -> PlatformManagerImpl.detach(event.server()));

        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
        EnvironmentExecutor.willSetup(EnvironmentType.COMMON);

        EntityLifecycleEvents.ALLOW_CLIMBING.register(this::onAllowClimbing);

        EntityLifecycleEvents.ALLOW_BED.register(this::onAllowBed);
        EntityLifecycleEvents.STOP_SLEEPING.register(this::onStopSleep);

        EntityLifecycleEvents.USE_BLOCK.register(this::onUseItemFirst);
        EntityLifecycleEvents.ATTACK_BLOCK.register(this::onBlockBreakPre);

        EnvironmentExecutor.didInit(EnvironmentType.COMMON);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.COMMON, FabricLoader.getInstance().getConfigDir());
//        CommonEventRegistries.getInstance().didServerStop(server -> {
//            FabricConfigTracker.INSTANCE.unloadConfigs(FabricConfig.Type.SERVER, FabricLoader.getInstance().getConfigDir());
//        });

        EnvironmentExecutor.didSetup(EnvironmentType.COMMON);
    }

    public OpenInteractionResult onUseItemFirst(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return OpenInteractionResult.PASS;
        }
        var itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof AbstractItemHandler handler) {
            return handler.useOnFirst(itemStack, new UseOnContext(player, hand, hitResult));
        }
        return OpenInteractionResult.PASS;
    }

    public OpenInteractionResult onAllowClimbing(LivingEntity entity, BlockPos blockPos, BlockState blockState) {
        if (entity.isSpectator()) {
            return OpenInteractionResult.PASS;
        }
        if (blockState.getBlock() instanceof AbstractBlock block && block.isLadder(blockState, entity.level(), blockPos, entity)) {
            return OpenInteractionResult.SUCCESS;
        }
        return OpenInteractionResult.PASS;
    }

    public OpenInteractionResult onAllowBed(LivingEntity entity, BlockPos sleepingPos, BlockState blockState, boolean vanillaResult) {
        if (blockState.getBlock() instanceof AbstractBlock block && block.isBed(blockState, entity.level(), sleepingPos, entity)) {
            return OpenInteractionResult.SUCCESS;
        }
        return OpenInteractionResult.PASS;
    }

    public void onStopSleep(LivingEntity entity, BlockPos sleepingPos) {
        var level = entity.level();
        var blockState = level.getBlockState(sleepingPos);
        if (blockState.getBlock() instanceof AbstractBlock block && block.isBed(blockState, level, sleepingPos, entity)) {
            entity.stopSleeping(sleepingPos);
        }
    }

    public OpenInteractionResult onBlockBreakPre(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) {
        if (player.isSpectator()) {
            return OpenInteractionResult.PASS;
        }
        var blockState = level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof AbstractBlock block)) {
            return OpenInteractionResult.PASS;
        }
        var result = block.attackBlock(level, pos, blockState, direction, player, AbstractInteractionHand.wrap(hand));
        if (result == OpenInteractionResult.CONSUME) {
            return OpenInteractionResult.FAIL;
        }
        if (result == OpenInteractionResult.SUCCESS) {
            return OpenInteractionResult.PASS;
        }
        return result;
    }
}
