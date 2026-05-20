package moe.plushie.armourers_workshop.init.platform.fabric.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
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
import moe.plushie.armourers_workshop.init.platform.fabric.event.common.FabricEntityEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;

public class CommonProxyImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        ArmourersWorkshop.init();

        // prioritize handle.
        EventBus.register(ServerStartingEvent.class, event -> PlatformManagerImpl.attach(event.server()));
        EventBus.register(ServerStoppedEvent.class, event -> PlatformManagerImpl.detach(event.server()));

        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
        EnvironmentExecutor.willSetup(EnvironmentType.COMMON);

        EventBus.register(FabricEntityEvent.StartClimbing.class, event -> {
            var entity = event.entity();
            if (entity.isSpectator()) {
                return;
            }
            var blockPos = event.blockPos();
            var blockState = event.blockState();
            if (blockState.getBlock() instanceof AbstractBlock block && block.isLadder(blockState, entity.level(), blockPos, entity)) {
                event.setResult(OpenInteractionResult.SUCCESS);
            }
        });
        EventBus.register(FabricEntityEvent.StartSleep.class, event -> {
            var entity = event.entity();
            var sleepingPos = event.sleepingPos();
            var blockState = event.blockState();
            if (blockState.getBlock() instanceof AbstractBlock block && block.isBed(blockState, entity.level(), sleepingPos, entity)) {
                event.setResult(OpenInteractionResult.SUCCESS);
            }
        });
        EventBus.register(FabricEntityEvent.StopSleep.class, event -> {
            var entity = event.entity();
            var sleepingPos = event.sleepingPos();
            var level = entity.level();
            var blockState = level.getBlockState(sleepingPos);
            if (blockState.getBlock() instanceof AbstractBlock block && block.isBed(blockState, level, sleepingPos, entity)) {
                entity.stopSleeping(sleepingPos);
            }
        });

        EventBus.register(FabricEntityEvent.UseBlock.class, event -> {
            var entity = event.entity();
            if (!(entity instanceof Player player) || player.isSpectator()) {
                return;
            }
            var hand = AbstractInteractionHand.unwrap(event.hand());
            var itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() instanceof AbstractItemHandler handler) {
                event.setResult(handler.useOnFirst(itemStack, new UseOnContext(player, hand, event.hitResult())));
            }
        });
        EventBus.register(FabricEntityEvent.AttackBlock.class, event -> {
            var entity = event.entity();
            if (!(entity instanceof Player player) || player.isSpectator()) {
                return;
            }
            var level = event.level();
            var pos = event.pos();
            var blockState = level.getBlockState(pos);
            if (!(blockState.getBlock() instanceof AbstractBlock block)) {
                return;
            }
            var result = block.attackBlock(level, pos, blockState, AbstractDirection.unwrap(event.direction()), player, event.hand());
            if (result == OpenInteractionResult.CONSUME) {
                event.setResult(OpenInteractionResult.FAIL);
                return;
            }
            if (result == OpenInteractionResult.SUCCESS) {
                return;
            }
            event.setResult(result);
        });

        EnvironmentExecutor.didInit(EnvironmentType.COMMON);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.COMMON, FabricLoader.getInstance().getConfigDir());
//        CommonEventRegistries.getInstance().didServerStop(server -> {
//            FabricConfigTracker.INSTANCE.unloadConfigs(FabricConfig.Type.SERVER, FabricLoader.getInstance().getConfigDir());
//        });

        EnvironmentExecutor.didSetup(EnvironmentType.COMMON);
    }

}
